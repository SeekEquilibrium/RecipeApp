package com.example.praksa.Controllers;

import com.example.praksa.Converters.RecipeCategoryConverter;
import com.example.praksa.DTOs.RecipeCategoryDTO;
import com.example.praksa.DTOs.RecipeCategoryListDTO;
import com.example.praksa.Models.RecipeCategory;
import com.example.praksa.Repositories.postgres.RecipeCategoryRepository;
import com.example.praksa.Services.FavouriteCategoryService;
import com.example.praksa.Services.RecipeCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/category")
public class RecipeCategoryController {
    private final RecipeCategoryRepository recipeCategoryRepository;
    private final RecipeCategoryConverter converter;
    private final RecipeCategoryService recipeCategoryService;
    private final FavouriteCategoryService favouriteCategoryService;

    public RecipeCategoryController(RecipeCategoryRepository recipeCategoryRepository, RecipeCategoryConverter converter, RecipeCategoryService recipeCategoryService, FavouriteCategoryService favouriteCategoryService) {
        this.recipeCategoryRepository = recipeCategoryRepository;
        this.converter = converter;
        this.recipeCategoryService = recipeCategoryService;
        this.favouriteCategoryService = favouriteCategoryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new recipe category", method = "POST")
    public ResponseEntity<RecipeCategoryDTO> addRecipeCategory(@RequestBody RecipeCategoryDTO categoryDTO){
        if(this.recipeCategoryRepository.getByName(categoryDTO.getName()) == null){
            RecipeCategory category = new RecipeCategory(categoryDTO.getName(), categoryDTO.getDescription());
            this.recipeCategoryRepository.save(category);
            return new ResponseEntity<>(converter.recipeCategoryToDTO(category), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null , HttpStatus.CONFLICT);
        }

    }
    @GetMapping(value ="/getAll")
    @Operation(summary = "Get all recipe categories", method = "GET")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getAll() throws Exception {
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        if(recipeCategoryList.isEmpty())
            throw new Exception("RecipeCategories are empty");
        return ResponseEntity.ok(convertCategoryListToDTO(recipeCategoryList));
    }

    @DeleteMapping
    @Operation(summary = "Deletes recipeCategory with the given name")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@RequestParam String categoryName) throws Exception {
        RecipeCategory category = recipeCategoryRepository.getByName(categoryName);
        if (category == null) return ResponseEntity.notFound().build();
        recipeCategoryService.deleteRecipeCategory(category.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value="/favourite/add")
    @Operation(summary = "Add the recipe category to your favourites ", method = "POST")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> addToFavourites (@RequestParam String categoryName) throws Exception {
        favouriteCategoryService.addFavouriteCategory(categoryName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/favourite/getAll")
    @Operation(summary = "Retrieve all favourite categories from user",method = "GET")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getAllFavourites() {
        Set<RecipeCategory> recipeCategoryList = favouriteCategoryService.getFavouritesForUser();
        return  ResponseEntity.ok(recipeCategoryList.stream().map(converter::recipeCategoryToDTO).collect(Collectors.toList()));
    }


    @DeleteMapping(value = "/favourite/delete")
    @Operation(summary = "Delete recipe category with given name from user's favourites",method = "DELETE")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteFavourite(@RequestParam String categoryName) throws Exception {
        favouriteCategoryService.deleteFavouriteCategory(categoryName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private RecipeCategoryListDTO convertCategoryListToDTO(List<RecipeCategory> recipeCategoryList) {
        List<RecipeCategoryDTO> recipeCategoryDTOList = recipeCategoryList.stream().map(converter::recipeCategoryToDTO).collect(Collectors.toList());
        return new RecipeCategoryListDTO(recipeCategoryDTOList);
    }


}



