package com.example.praksa.Converters;

import com.example.praksa.DTOs.IngredientDTO;
import com.example.praksa.Models.Ingredient;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class IngredientDTOConverter {

    public Ingredient DTOToIngredient(IngredientDTO dto){
        return Ingredient.builder()
                .name(dto.getName())
                .build();
    }
    public IngredientDTO IngredientToDTO(Ingredient ingredient){
        return IngredientDTO.builder()
                .name(ingredient.getName())
                .build();
    }
}
