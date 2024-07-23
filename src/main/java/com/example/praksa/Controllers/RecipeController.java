package com.example.praksa.Controllers;

import com.example.praksa.Converters.RecipeDTOConverter;
import com.example.praksa.DTOs.*;
import com.example.praksa.Models.*;
import com.example.praksa.Services.FavouriteRecipeService;
import com.example.praksa.Services.RecipeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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
    @ApiOperation(value = "Create a new recipe",httpMethod = "POST")
    public ResponseEntity<?> addRecipe(@ModelAttribute RecipeDTO recipeDTO , @RequestPart("image") MultipartFile image) throws Exception {

        recipeService.create(recipeDTO,recipeDTO.getIngredientRequestDTOList(),image);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/getAll")
    @ApiOperation(value = "Retrieves all recipes",httpMethod = "GET")
    public ResponseEntity<?> getAll(){
        List<RecipeResponseDTO> responseDTOS = recipeService.getAll();
        return ResponseEntity.ok(responseDTOS);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Deletes a recipe with the given id")
    public ResponseEntity<?> delete(@RequestParam Long id) throws Exception{
        recipeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value="/favourite/add/{recipeId}")
    @ApiOperation(value = "Add the recipe to your favourites ", httpMethod = "POST")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> addToFavourites (@PathVariable Long recipeId) throws Exception {
        favouriteRecipeService.addFavouriteRecipe(recipeId);
        return new ResponseEntity<>(String.format("Recipe  %d successfully added to favourites",recipeId),HttpStatus.CREATED);
    }

    @GetMapping(value = "/favourite/getAll")
    @ApiOperation(value = "Retrieve all favourite recipes from user",httpMethod = "GET")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getAllFavourites() {
        List<RecipeResponseDTO> recipeList = favouriteRecipeService.getFavouritesForUser();
        return  ResponseEntity.ok(recipeList);
    }



    @DeleteMapping(value = "/favourite/delete/{recipeId}")
    @ApiOperation(value = "Delete recipe  withe given id from user's favourites",httpMethod = "DELETE")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteFavourite(@PathVariable Long recipeId) throws Exception {
        favouriteRecipeService.deleteFavouriteCategory(recipeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/filter/category")
    @ApiOperation(value = "Search for all the recipes with the given category",httpMethod = "GET")
    public ResponseEntity<?> searchCategory(@RequestParam String categoryName) throws Exception {
        List<RecipeResponseDTO> responseDTOS = recipeService.searchCategory(categoryName);
        return ResponseEntity.ok(responseDTOS);
    }

    @GetMapping(value = "/filter/ingredient")
    @ApiOperation(value = "Search for all the recipes with the given ingredient",httpMethod = "GET")
    public ResponseEntity<?> searchIngredient(@RequestParam String ingredientName) throws Exception{
            List<RecipeResponseDTO> responseDTOS = recipeService.searchIngredient(ingredientName);
            return ResponseEntity.ok(responseDTOS);
    }

}
