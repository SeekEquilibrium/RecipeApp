package com.example.praksa.DTOs;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredientDTO {
    private Long recipeId;
    private String ingredientName;
    @NotNull
    private String amount;
}
