package com.example.praksa.Controllers;

import com.example.praksa.DTOs.RecipeIngredientDTO;
import com.example.praksa.DTOs.RecipeIngredientRequestDTO;
import com.example.praksa.Models.RecipeIngredient;
import com.example.praksa.Services.RecipeIngredientService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipeIngredient")
public class RecipeIngredientController {

    private final RecipeIngredientService recipeIngredientService;

    public RecipeIngredientController(RecipeIngredientService recipeIngredientService) {
        this.recipeIngredientService = recipeIngredientService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "Add an ingredient to existing Recipe with the given id",httpMethod = "POST")
    public ResponseEntity<RecipeIngredientRequestDTO> addRecipeIngredient(@RequestBody RecipeIngredientDTO recipeIngredientDTO) throws Exception {
        RecipeIngredientRequestDTO recipeIngredient = recipeIngredientService.create(recipeIngredientDTO);
        return new ResponseEntity<>(recipeIngredient, HttpStatus.CREATED);
    }
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "Delete an ingredient from the Recipe with the given id", httpMethod = "DELETE")
    public ResponseEntity<?> deleteIngredientFromRecipe(@PathVariable Long recipeId , Long ingredientId) throws Exception{
        recipeIngredientService.deleteIngredientFromRecipe(recipeId,ingredientId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
