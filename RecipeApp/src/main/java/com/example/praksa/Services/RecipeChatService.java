package com.example.praksa.Services;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeChatService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RecipeChatService(VectorStore vectorStore, AnthropicChatModel anthropicChatModel) {
        this.vectorStore = vectorStore;
        this.chatClient = ChatClient.builder(anthropicChatModel).build();
    }

    public String chat(String userMessage) {
        List<Document> relevant = vectorStore.similaritySearch(
                SearchRequest.builder().query(userMessage).topK(3).build()
        );

        String context = relevant.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n---\n\n"));

        String systemPrompt = "You are a helpful recipe assistant. Answer the user's cooking question " +
                "using ONLY the recipes provided below. Include ingredients and step-by-step " +
                "instructions in your response. If none of the recipes are relevant, say so.\n\n" +
                "Recipes:\n" + context;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
    }
}