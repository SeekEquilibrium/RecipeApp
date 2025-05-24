package com.example.praksa.Models;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RecipeCSV {
    private String name;
    private String cookTime;
    private String prepTime;
    private String recipeCategory;
    private List<String> keywords;

    private List<String> ingredientQuantities;
    private List<String> ingredientParts;

    private int calories;
    private String fatContent;
    private String carbohydrateContent;
    private String fiberContent;
    private String sugarContent;
    private String proteinContent;

    private int servings;
    private String instructions;

    public Map<String, String> getIngredientMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < ingredientParts.size(); i++) {
            String part = ingredientParts.get(i);
            String qty = (i < ingredientQuantities.size()) ? ingredientQuantities.get(i) : "N/A";
            map.put(part, qty);
        }
        return map;
    }
}
