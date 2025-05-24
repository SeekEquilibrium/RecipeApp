package com.example.praksa.DTOs;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeResponseDTO {
    private String name;
    private String servings;
    private String preparation;
    private String recipeCategoryName;
    private String recipeCategoryDescription;
    private List<RecipeIngredientRequestDTO> ingredientList;
    private String imageName;
    private String createdDateTime;
}
