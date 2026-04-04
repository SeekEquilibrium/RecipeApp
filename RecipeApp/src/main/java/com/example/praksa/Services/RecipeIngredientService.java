package com.example.praksa.Services;

import com.example.praksa.Converters.RecipeIngredientDTOConverter;
import com.example.praksa.DTOs.RecipeIngredientDTO;
import com.example.praksa.DTOs.RecipeIngredientRequestDTO;
import com.example.praksa.Models.Ingredient;
import com.example.praksa.Models.Recipe;
import com.example.praksa.Models.RecipeIngredient;
import com.example.praksa.Repositories.postgres.IngredientRepository;
import com.example.praksa.Repositories.postgres.RecipeIngredientRepository;
import com.example.praksa.Repositories.postgres.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class RecipeIngredientService {
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientService ingredientService;
    private final RecipeIngredientDTOConverter converter;

    public RecipeIngredientService(RecipeIngredientRepository recipeIngredientRepository, IngredientRepository ingredientRepository, RecipeRepository recipeRepository, IngredientService ingredientService, RecipeIngredientDTOConverter converter) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
        this.converter = converter;
    }
    @Transactional
    public RecipeIngredientRequestDTO create(RecipeIngredientDTO recipeIngredientDTO) throws Exception {
        if(ingredientRepository.existsByNameIgnoreCase(recipeIngredientDTO.getIngredientName())){
            Ingredient ingredient = ingredientRepository.findByName(recipeIngredientDTO.getIngredientName());
            if(recipeIngredientRepository.existsByRecipeIdAndIngredientId(recipeIngredientDTO.getRecipeId(), ingredient.getId())){
                log.error("Ingredient already added to the recipe");
                throw  new Exception("Ingredient already added to the recipe");
            } else {
                Optional<Recipe> recipe = recipeRepository.findById(recipeIngredientDTO.getRecipeId());
                Recipe recipe1 = recipe.get();
                RecipeIngredient recipeIngredient = new RecipeIngredient(recipeIngredientDTO.getAmount(),ingredient,recipe1);
                recipe1.addRecipeIngredient(recipeIngredient);
                recipeRepository.save(recipe1);
                return converter.recipeIngredientToDTO(recipeIngredient);
            }
        }else {
            Ingredient ingredient = ingredientService.create(recipeIngredientDTO.getIngredientName());
            Recipe recipe = recipeRepository.findById(recipeIngredientDTO.getRecipeId()).orElseThrow();
            RecipeIngredient recipeIngredient = new RecipeIngredient(recipeIngredientDTO.getAmount(),ingredient,recipe);
            recipe.addRecipeIngredient(recipeIngredient);
            recipeRepository.save(recipe);
            return converter.recipeIngredientToDTO(recipeIngredient);
        }

    }

    @Transactional
    public void deleteIngredientFromRecipe(Long recipeId , Long ingredientId) throws Exception{
        RecipeIngredient recipeIngredient = recipeIngredientRepository.findByRecipeIdAndIngredientId(recipeId,ingredientId).orElseThrow(()->{
            log.error("Ingredient not found");
            return new Exception("Ingredient not found");
        });
        Recipe recipe = recipeRepository.getReferenceById(recipeId);
        recipe.removeRecipeIngredient(recipeIngredient);
        recipeRepository.save(recipe);
        recipeIngredientRepository.delete(recipeIngredient);
    }
}
