package com.example.praksa.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeBotResponse {
    private String recipeName;
    private String description;
    private String cuisine;
    private String diet;
    private String course;
    private String prepTime;
    private String cookTime;
    private List<Ingredient> ingredients;
    private List<String> steps;
    private String answer;
    private String error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ingredient {
        private String name;
        private String quantity;
    }
}
