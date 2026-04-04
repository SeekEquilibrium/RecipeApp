package com.example.praksa.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeIngredientRequestDTO {
    private String ingredientName;
    @NotNull
    private String amount;
}
