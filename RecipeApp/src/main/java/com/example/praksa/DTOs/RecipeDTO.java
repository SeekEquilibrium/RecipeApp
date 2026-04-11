package com.example.praksa.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data

public class RecipeDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String preparation;
    @NotBlank
    private String servings;
    @NotBlank
    private String recipeCategory;
    @NotBlank
    private List<RecipeIngredientRequestDTO> ingredientRequestDTOList;

}
