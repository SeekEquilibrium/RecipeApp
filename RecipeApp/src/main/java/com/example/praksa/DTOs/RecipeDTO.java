package com.example.praksa.DTOs;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data

public class RecipeDTO {
    private String name;
    private String preparation;
    private String servings;
    private String recipeCategory;
    private List<RecipeIngredientRequestDTO> ingredientRequestDTOList;

}
