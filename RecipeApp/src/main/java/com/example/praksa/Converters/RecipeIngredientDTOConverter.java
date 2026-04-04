package com.example.praksa.Converters;

import com.example.praksa.DTOs.RecipeIngredientRequestDTO;
import com.example.praksa.Models.RecipeIngredient;
import org.springframework.stereotype.Service;

@Service
public class RecipeIngredientDTOConverter {

    public RecipeIngredientRequestDTO recipeIngredientToDTO(RecipeIngredient recipeIngredient){
        return RecipeIngredientRequestDTO.builder()
                .ingredientName(recipeIngredient.getIngredient().getName())
                .amount(recipeIngredient.getAmount())
                .build();
    }

}
