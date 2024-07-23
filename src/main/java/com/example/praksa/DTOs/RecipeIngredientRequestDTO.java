package com.example.praksa.DTOs;

import com.sun.istack.NotNull;
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
