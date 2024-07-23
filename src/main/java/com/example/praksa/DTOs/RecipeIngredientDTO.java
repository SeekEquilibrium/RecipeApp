package com.example.praksa.DTOs;

import com.sun.istack.NotNull;
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
