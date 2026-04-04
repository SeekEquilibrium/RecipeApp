package com.example.praksa.Controllers;

import com.example.praksa.DTOs.ChatRequest;
import com.example.praksa.Services.RecipeChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipeBot")
public class RecipeChatController {

    private final RecipeChatService recipeChatService;

    public RecipeChatController(RecipeChatService recipeChatService) {
        this.recipeChatService = recipeChatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(recipeChatService.chat(request.getMessage()));
    }
}