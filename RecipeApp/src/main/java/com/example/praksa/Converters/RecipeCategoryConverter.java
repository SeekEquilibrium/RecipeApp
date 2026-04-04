package com.example.praksa.Converters;

import com.example.praksa.DTOs.CountCategoryDTO;
import com.example.praksa.DTOs.CountCategoryProjection;
import com.example.praksa.DTOs.RecipeCategoryDTO;
import com.example.praksa.Models.RecipeCategory;
import org.springframework.stereotype.Service;

@Service
public class RecipeCategoryConverter {

    public RecipeCategory DTOToRecipeCategory(RecipeCategoryDTO dto){
        return RecipeCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
    public RecipeCategoryDTO recipeCategoryToDTO(RecipeCategory recipeCategory){
        return RecipeCategoryDTO.builder()
                .name(recipeCategory.getName())
                .description(recipeCategory.getDescription())
                .build();
    }

    public CountCategoryDTO projectionToDTO(CountCategoryProjection projection){
        return CountCategoryDTO.builder()
                .categoryName(projection.getCategoryName())
                .count(projection.getCount())
                .build();
    }
}
