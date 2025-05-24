package com.example.praksa.Controllers;

import com.example.praksa.DTOs.RecipeDTO;
import com.example.praksa.DTOs.RecipeResponseDTO;
import com.example.praksa.Services.FavouriteRecipeService;
import com.example.praksa.Services.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping(value = "/recipe")
public class RecipeController {

    private final RecipeService recipeService;
    private final FavouriteRecipeService favouriteRecipeService;


    public RecipeController(RecipeService recipeService, FavouriteRecipeService favouriteRecipeService) {
        this.recipeService = recipeService;
        this.favouriteRecipeService = favouriteRecipeService;

    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Operation(summary = "Create a new recipe",method = "POST")
    public ResponseEntity<?> addRecipe(@ModelAttribute RecipeDTO recipeDTO , @RequestPart("image") MultipartFile image) throws Exception {

        recipeService.create(recipeDTO,recipeDTO.getIngredientRequestDTOList(),image);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/getAll")
    @Operation(summary = "Retrieves all recipes",method = "GET")
    public ResponseEntity<?> getAll(){
        List<RecipeResponseDTO> responseDTOS = recipeService.getAll();
        return ResponseEntity.ok(responseDTOS);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Deletes a recipe with the given id")
    public ResponseEntity<?> delete(@RequestParam Long id) throws Exception{
        recipeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value="/favourite/add/{recipeId}")
    @Operation(summary = "Add the recipe to your favourites ", method = "POST")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> addToFavourites (@PathVariable Long recipeId) throws Exception {
        favouriteRecipeService.addFavouriteRecipe(recipeId);
        return new ResponseEntity<>("Recipe  %d successfully added to favourites".formatted(recipeId),HttpStatus.CREATED);
    }

    @GetMapping(value = "/favourite/getAll")
    @Operation(summary = "Retrieve all favourite recipes from user",method = "GET")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getAllFavourites() {
        List<RecipeResponseDTO> recipeList = favouriteRecipeService.getFavouritesForUser();
        return  ResponseEntity.ok(recipeList);
    }



    @DeleteMapping(value = "/favourite/delete/{recipeId}")
    @Operation(summary = "Delete recipe  withe given id from user's favourites",method = "DELETE")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteFavourite(@PathVariable Long recipeId) throws Exception {
        favouriteRecipeService.deleteFavouriteCategory(recipeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/filter/category")
    @Operation(summary = "Search for all the recipes with the given category",method = "GET")
    public ResponseEntity<?> searchCategory(@RequestParam String categoryName) throws Exception {
        List<RecipeResponseDTO> responseDTOS = recipeService.searchCategory(categoryName);
        return ResponseEntity.ok(responseDTOS);
    }

    @GetMapping(value = "/filter/ingredient")
    @Operation(summary = "Search for all the recipes with the given ingredient",method = "GET")
    public ResponseEntity<?> searchIngredient(@RequestParam String ingredientName) throws Exception{
            List<RecipeResponseDTO> responseDTOS = recipeService.searchIngredient(ingredientName);
            return ResponseEntity.ok(responseDTOS);
    }

}
