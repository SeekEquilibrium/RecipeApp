package com.example.praksa.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseListDTO {
    private List<RecipeResponseDTO> recipeResponseDTOS;
}
