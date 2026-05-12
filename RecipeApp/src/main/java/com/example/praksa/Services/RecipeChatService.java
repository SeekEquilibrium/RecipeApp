package com.example.praksa.Services;

import com.example.praksa.DTOs.RecipeBotResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeChatService {

    private static final Logger log = LoggerFactory.getLogger(RecipeChatService.class);

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final AnthropicChatModel anthropicChatModel;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RecipeChatService(VectorStore vectorStore, AnthropicChatModel anthropicChatModel, JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.anthropicChatModel = anthropicChatModel;
        this.jdbcTemplate = jdbcTemplate;
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();
        this.chatClient = ChatClient.builder(anthropicChatModel)
                .defaultTemplateRenderer(StTemplateRenderer.builder()
                        .startDelimiterToken('<')
                        .endDelimiterToken('>')
                        .build())
                .defaultSystem("""
                        You are a friendly and knowledgeable recipe assistant.

                        Retrieved recipe context:
                        <context>

                        Respond with valid JSON in one of three formats depending on the request.
                        Be concise: description max 1 sentence, steps max 10 words each, no filler text.

                        1. If the user asks for a recipe, return the full recipe structure:
                        {
                          "recipeName": "...",
                          "description": "one sentence max",
                          "cuisine": "...",
                          "diet": "...",
                          "course": "...",
                          "prepTime": "...",
                          "cookTime": "...",
                          "ingredients": [{"name": "ingredient name", "quantity": "amount"}],
                          "steps": ["brief step 1", "brief step 2"]
                        }

                        2. If the user asks a conversational question about a recipe already discussed
                        (e.g. "is it vegan?", "how long does it take?", "what course is it?",
                        "can I substitute X?", "make it a meal"), return:
                        {"answer": "1-2 sentence answer"}

                        3. If no matching recipe is in the context, return exactly:
                        {"error": "No matching recipe found. Try rephrasing or asking about a different dish."}

                        Do not invent recipes not present in the context.
                        IMPORTANT: output the raw JSON object only. No markdown, no code fences, no backticks, no extra text.
                        """)
                .build();
    }

    public RecipeBotResponse chat(String userMessage, String sessionId) {
        String key = (sessionId != null && !sessionId.isBlank()) ? sessionId : "default";

        String rewrittenQuery = rewriteQuery(userMessage, key);

        List<Document> vectorDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(rewrittenQuery)
                        .topK(15)
                        .similarityThreshold(0.55)
                        .build());

        List<Document> ftsDocs = fullTextSearch(rewrittenQuery, 15);
        List<Document> merged = reciprocalRankFusion(vectorDocs, ftsDocs, 6);

        String context = merged.isEmpty()
                ? "No recipes found."
                : merged.stream().map(Document::getText).collect(Collectors.joining("\n\n---\n\n"));

        try {
            String raw = chatClient.prompt()
                    .system(s -> s.param("context", context))
                    .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(key).build())
                    .user(userMessage)
                    .call()
                    .content();
            return parseResponse(raw);
        } catch (Throwable e) {
            log.error("Chat call failed: {}", e.getMessage());
            return errorResponse();
        }
    }

    private String rewriteQuery(String userMessage, String conversationId) {
        List<Message> history = chatMemory.get(conversationId);
        if (history == null || history.isEmpty()) {
            return userMessage;
        }

        String historySection = "Conversation history:\n" + history.stream()
                .map(m -> m.getMessageType().name() + ": " + m.getText())
                .collect(Collectors.joining("\n")) + "\n\n";

        try {
            return ChatClient.builder(anthropicChatModel).build()
                    .prompt()
                    .system("""
                            You help rewrite user messages into concise recipe search queries.
                            Use the conversation history to resolve any pronouns or vague references \
                            (e.g. "it", "that dish", "make it a meal") to the actual recipe or ingredients discussed.
                            Focus only on dish names and key ingredients.
                            Return ONLY the search query — a short phrase, nothing else.\
                            """)
                    .user(historySection + "User message: " + userMessage)
                    .call()
                    .content();
        } catch (Exception e) {
            log.warn("Query rewrite failed, using original message: {}", e.getMessage());
            return userMessage;
        }
    }

    private List<Document> fullTextSearch(String query, int limit) {
        String sql = """
                SELECT id::text, content, metadata::text
                FROM vector_store
                WHERE to_tsvector('english', content) @@ plainto_tsquery('english', ?)
                ORDER BY ts_rank(to_tsvector('english', content), plainto_tsquery('english', ?)) DESC
                LIMIT ?
                """;
        return jdbcTemplate.query(sql,
                (rs, i) -> new Document(
                        rs.getString("id"),
                        rs.getString("content"),
                        parseMetadata(rs.getString("metadata"))),
                query, query, limit);
    }

    private List<Document> reciprocalRankFusion(List<Document> vectorDocs, List<Document> ftsDocs, int topN) {
        Map<String, Double> scores = new LinkedHashMap<>();
        Map<String, Document> docMap = new LinkedHashMap<>();
        int k = 60;
        for (int i = 0; i < vectorDocs.size(); i++) {
            Document d = vectorDocs.get(i);
            scores.merge(d.getId(), 1.0 / (k + i + 1), Double::sum);
            docMap.put(d.getId(), d);
        }
        for (int i = 0; i < ftsDocs.size(); i++) {
            Document d = ftsDocs.get(i);
            scores.merge(d.getId(), 1.0 / (k + i + 1), Double::sum);
            docMap.put(d.getId(), d);
        }
        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topN)
                .map(e -> docMap.get(e.getKey()))
                .toList();
    }

    private RecipeBotResponse parseResponse(String raw) {
        if (raw == null) return errorResponse();
        // Strip any markdown code fences regardless of leading whitespace
        String json = raw.strip().replaceAll("(?s)^```[a-z]*\\s*", "").replaceAll("\\s*```$", "").strip();
        // Extract the JSON object — first { to last } — to discard any surrounding text
        int start = json.indexOf('{');
        int end = json.lastIndexOf('}');
        if (start != -1 && end > start) {
            json = json.substring(start, end + 1);
        }
        try {
            return objectMapper.readValue(json, RecipeBotResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse Claude response: [{}]", raw);
            return errorResponse();
        }
    }

    private RecipeBotResponse errorResponse() {
        RecipeBotResponse r = new RecipeBotResponse();
        r.setError("Sorry, I had trouble processing that response. Please try again.");
        return r;
    }

    private Map<String, Object> parseMetadata(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
}
