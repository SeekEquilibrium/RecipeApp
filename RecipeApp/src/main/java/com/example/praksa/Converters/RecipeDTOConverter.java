package com.example.praksa.Converters;

import com.example.praksa.DTOs.RecipeResponseDTO;
import com.example.praksa.Models.Recipe;
import com.example.praksa.Services.ImageService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class RecipeDTOConverter {

    private final ImageService imageService;
    private final RecipeIngredientDTOConverter converter;

    public RecipeDTOConverter(ImageService imageService, RecipeIngredientDTOConverter converter) {

        this.imageService = imageService;
        this.converter = converter;
    }

    public RecipeResponseDTO recipeToDTO(Recipe recipe){
        return RecipeResponseDTO.builder()
                .name(recipe.getName())
                .recipeCategoryName(recipe.getRecipeCategory().getName())
                .recipeCategoryDescription(recipe.getRecipeCategory().getDescription())
                .ingredientList(recipe.getRecipeIngredients().stream().map(converter::recipeIngredientToDTO).collect(Collectors.toList()))
                .servings(recipe.getServings())
                .preparation(recipe.getPreparation())
                .imageName(imageService.getImageName(recipe.getImage().getId()))
                .createdDateTime(recipe.getCreatedDateTime().toString())
                .build();
    }
}
