package com.example.praksa.Controllers;

import com.example.praksa.Converters.RecipeCategoryConverter;
import com.example.praksa.DTOs.RecipeCategoryDTO;
import com.example.praksa.DTOs.RecipeCategoryListDTO;
import com.example.praksa.Models.RecipeCategory;
import com.example.praksa.Repositories.RecipeCategoryRepository;
import com.example.praksa.Services.FavouriteCategoryService;
import com.example.praksa.Services.RecipeCategoryService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "Create a new recipe category", httpMethod = "POST")
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
    @ApiOperation(value = "Get all recipe categories", httpMethod = "GET")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getAll() throws Exception {
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        if(recipeCategoryList.isEmpty())
            throw new Exception("RecipeCategories are empty");
        return ResponseEntity.ok(convertCategoryListToDTO(recipeCategoryList));
    }

    @DeleteMapping
    @ApiOperation(value = "Deletes recipeCategory with the given id")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> delete(@RequestParam Long id) throws Exception{
        recipeCategoryService.deleteRecipeCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value="/favourite/add/{categoryId}")
    @ApiOperation(value = "Add the recipe category to your favourites ", httpMethod = "POST")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> addToFavourites (@PathVariable Long categoryId) throws Exception {
        favouriteCategoryService.addFavouriteCategory(categoryId);
        return new ResponseEntity<>(String.format("Recipe category %d successfully added to favourites",categoryId),HttpStatus.CREATED);
    }

    @GetMapping(value = "/favourite/getAll")
    @ApiOperation(value = "Retrieve all favourite categories from user",httpMethod = "GET")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getAllFavourites() {
        Set<RecipeCategory> recipeCategoryList = favouriteCategoryService.getFavouritesForUser();
        return  ResponseEntity.ok(recipeCategoryList.stream().map(converter::recipeCategoryToDTO).collect(Collectors.toList()));
    }


    @DeleteMapping(value = "/favourite/delete/{categoryId}")
    @ApiOperation(value = "Delete recipe category withe given id from user's favourites",httpMethod = "DELETE")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteFavourite(@PathVariable Long categoryId) throws Exception {
        favouriteCategoryService.deleteFavouriteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private RecipeCategoryListDTO convertCategoryListToDTO(List<RecipeCategory> recipeCategoryList) {
        List<RecipeCategoryDTO> recipeCategoryDTOList = recipeCategoryList.stream().map(converter::recipeCategoryToDTO).collect(Collectors.toList());
        return new RecipeCategoryListDTO(recipeCategoryDTOList);
    }


}



