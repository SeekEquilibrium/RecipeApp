package com.example.praksa.Services;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class RecipeChatService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public RecipeChatService(VectorStore vectorStore, AnthropicChatModel anthropicChatModel) {
        this.vectorStore = vectorStore;
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();
        this.chatClient = ChatClient.builder(anthropicChatModel)
                .defaultSystem("You are a helpful recipe assistant. Answer the user's cooking " +
                        "question using ONLY the recipes provided as context. Include ingredients " +
                        "and step-by-step instructions. If no recipes are relevant, say so clearly.")
                .build();
    }

    public String chat(String userMessage, String sessionId) {
        String key = (sessionId != null && !sessionId.isBlank()) ? sessionId : "default";

        return chatClient.prompt()
                .advisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(5)
                                        .similarityThreshold(0.65)
                                        .build())
                                .build(),
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId(key)
                                .build()
                )
                .user(userMessage)
                .call()
                .content();
    }
}
