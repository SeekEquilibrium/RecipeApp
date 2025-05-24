package com.example.praksa.Services;

import com.example.praksa.Converters.RecipeCategoryConverter;
import com.example.praksa.Converters.RecipeDTOConverter;
import com.example.praksa.DTOs.*;
import com.example.praksa.Models.*;
import com.example.praksa.Repositories.postgres.IngredientRepository;
import com.example.praksa.Repositories.postgres.RecipeCategoryRepository;
import com.example.praksa.Repositories.postgres.RecipeIngredientRepository;
import com.example.praksa.Repositories.postgres.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeCategoryRepository recipeCategoryRepository;
    private final ImageService imageService;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientService ingredientService;
    private final IngredientRepository ingredientRepository;
    private final RecipeDTOConverter converter;
    private final RecipeCategoryConverter categoryConverter;

    public RecipeService(RecipeRepository recipeRepository, RecipeCategoryRepository recipeCategoryRepository, ImageService imageService, RecipeIngredientRepository recipeIngredientRepository, IngredientService ingredientService, IngredientRepository ingredientRepository, RecipeDTOConverter converter, RecipeCategoryConverter categoryConverter) {
        this.recipeRepository = recipeRepository;
        this.recipeCategoryRepository = recipeCategoryRepository;
        this.imageService = imageService;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientService = ingredientService;
        this.ingredientRepository = ingredientRepository;
        this.converter = converter;
        this.categoryConverter = categoryConverter;
    }


    public Recipe create(RecipeDTO recipeDTO, List<RecipeIngredientRequestDTO> ingredientList, MultipartFile image) throws Exception {


        RecipeCategory recipeCategory = recipeCategoryRepository.findByName(recipeDTO.getRecipeCategory()).orElseThrow(() -> {
            log.error("RecipeCategory not found");
            return new Exception("RecipeCategory not found");
        });
        final Recipe recipe = new Recipe(recipeDTO.getName(), recipeDTO.getPreparation(), recipeDTO.getServings(), null, null, new ArrayList<>());
        recipe.setRecipeCategory(recipeCategory);

        Image recipeImage = imageService.addImage(image);
        recipe.setImage(recipeImage);

        recipe.setCreatedDateTime(LocalDateTime.now());

        ingredientList.stream().forEach(recipeIngredientDTO -> {
            if (ingredientRepository.existsByNameIgnoreCase(recipeIngredientDTO.getIngredientName())) {
                Ingredient ingredient = ingredientRepository.findByName(recipeIngredientDTO.getIngredientName());
                if (recipeIngredientRepository.existsByRecipeIdAndIngredientId(recipe.getId(), ingredient.getId())) {
                    log.error("Ingredient already added to the recipe");
                } else {
                    RecipeIngredient recipeIngredient = new RecipeIngredient(recipeIngredientDTO.getAmount(), ingredient, recipe);
                    recipe.addRecipeIngredient(recipeIngredient);


                }
            }
            else{
                Ingredient ingredient;
                try {
                    ingredient = ingredientService.create(recipeIngredientDTO.getIngredientName());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                RecipeIngredient recipeIngredient = new RecipeIngredient(recipeIngredientDTO.getAmount(), ingredient, recipe);
                recipe.addRecipeIngredient(recipeIngredient);
            }

        }
        );

        recipeRepository.save(recipe);
        return recipe;
    }

    public List<RecipeResponseDTO> getAll(){
        List<Recipe> recipes = recipeRepository.findAll();
        if(recipes.isEmpty()){
            log.error("Recipe list is empty");
            throw new RuntimeException("Recipe list is empty");
        }
        List<RecipeResponseDTO> list = recipes.stream().map(converter::recipeToDTO).toList();
        return list;
    }

    public void delete(Long id) throws Exception{
        Recipe recipe = recipeRepository.findById(id).orElseThrow(()->{
            log.error("Recipe  not found");
            return new Exception("Recipe  not found");
        });
        recipeRepository.delete(recipe);
    }
    @Transactional
    public List<RecipeResponseDTO> searchCategory(String categoryName) throws Exception {
        RecipeCategory recipeCategory = recipeCategoryRepository.findByName(categoryName).orElseThrow(() -> {
            log.error("RecipeCategory not found");
            return new Exception("RecipeCategory not found");
        });
        List<Recipe> recipes = recipeRepository.findAllWithCategory(categoryName);
        return recipes.stream().map(converter::recipeToDTO).toList();

    }

    @Transactional
    public List<RecipeResponseDTO> searchIngredient(String ingredientName) throws Exception {
        Ingredient ingredient = ingredientRepository.findByName(ingredientName);
        if(ingredient == null){
            log.error("Ingredient not found");
            throw  new Exception("Ingredient not found");
        }
        List<Recipe> recipes = recipeRepository.findAllWithIngredient(ingredientName);
        return recipes.stream().map(converter::recipeToDTO).toList();
    }

    public List<CountCategoryDTO> countCategoryAll(){

        List<CountCategoryProjection> countCategoryDTOS = recipeRepository.countCategoryAll();
        return countCategoryDTOS.stream().map(categoryConverter::projectionToDTO).toList();

    }

    public CountCategoryDTO countCategorySpecific(String categoryName) throws Exception {
        RecipeCategory recipeCategory = recipeCategoryRepository.findByName(categoryName).orElseThrow(() -> {
            log.error("RecipeCategory not found");
            return new Exception("RecipeCategory not found");
        });
        CountCategoryProjection projection = recipeRepository.countCategorySpecific(recipeCategory.getName());
        return new CountCategoryDTO(projection.getCategoryName(), projection.getCount());
    }

    public Integer countDate(LocalDate date){

       return recipeRepository.countByCreatedDateTimeBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    @Transactional
    public List<RecipeResponseDTO> retrieveDate(LocalDate date){
        List<Recipe> recipes = recipeRepository.findByCreatedDateTimeBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        return recipes.stream().map(converter::recipeToDTO).toList();
    }
}