package com.example.praksa.Services;

import com.example.praksa.Converters.IngredientDTOConverter;
import com.example.praksa.DTOs.IngredientDTO;
import com.example.praksa.DTOs.RecipeCategoryDTO;
import com.example.praksa.Models.Ingredient;
import com.example.praksa.Repositories.IngredientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientDTOConverter converter;

    public IngredientService(IngredientRepository ingredientRepository, IngredientDTOConverter converter) {
        this.ingredientRepository = ingredientRepository;
        this.converter = converter;
    }

    public Ingredient  create(String name) throws Exception {
        if(ingredientRepository.existsByNameIgnoreCase(name)){
            log.error("Ingredient already exists");
            throw new Exception("Ingredient already exists");
        }
        Ingredient ingredient = new Ingredient(name);
        ingredientRepository.save(ingredient);
        return ingredient;
    }

    public List<IngredientDTO> getAll()throws Exception {
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        if(ingredientList.isEmpty()) {
            log.error("Ingredient list is empty");
            throw new Exception("Ingredient list is empty");
        }
        List<IngredientDTO> ingredientDTOList = ingredientList.stream().map(converter::IngredientToDTO).toList();
        return ingredientDTOList;
    }
    public void deleteIngredient(Long id) throws Exception {
        Ingredient ingredient =ingredientRepository.findById(id).orElseThrow(()->{
            log.error("Ingredient not found");
            return new Exception("Ingredient not found");
        });
        ingredientRepository.delete(ingredient);
    }
}
