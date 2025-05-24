package com.example.praksa.Services;

import com.example.praksa.Converters.RecipeDTOConverter;
import com.example.praksa.DTOs.RecipeResponseDTO;
import com.example.praksa.Models.Recipe;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Repositories.postgres.RecipeRepository;
import com.example.praksa.Repositories.postgres.UserAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FavouriteRecipeService {

    private final RecipeRepository recipeRepository;
    private final UserAppRepository userAppRepository;
    private final RecipeDTOConverter converter;

    public FavouriteRecipeService(RecipeRepository recipeRepository, UserAppRepository userAppRepository, RecipeDTOConverter converter) {
        this.recipeRepository = recipeRepository;
        this.userAppRepository = userAppRepository;
        this.converter = converter;
    }

    @Transactional
    public void addFavouriteRecipe(Long recipeId) throws Exception {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> {
            log.error("Recipe category not found");
            throw new RuntimeException("Recipe not found");
        });
        if (recipeRepository.findByRecipeIdAndUserAppId(recipe.getId(),userApp.getId()).isPresent()) {
            log.error("Recipe already added");
            throw new Exception("Recipe  already added");
        }
        Set<Recipe> favRecipes = userAppRepository.getFavRecipes(userApp.getId());
        favRecipes.add(recipe);
        userApp.setFavouriteRecipes(favRecipes);
        userAppRepository.save(userApp);
    }
    @Transactional
    public List<RecipeResponseDTO> getFavouritesForUser() {
        UserApp user = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Recipe> favoritesList = userAppRepository.getFavRecipes(user.getId()).stream().toList();
        return favoritesList.stream().map(converter::recipeToDTO).toList();
    }

    public void deleteFavouriteCategory(Long recipeId) throws Exception {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> {
            log.error("Recipe  not found");
            throw new RuntimeException("Recipe");
        });
        if (recipeRepository.findByRecipeIdAndUserAppId(recipe.getId(),userApp.getId()).isEmpty()) {
            log.error("Recipe  not in favourites");
            throw new Exception("Recipe  not in favourites");
        }
        Set<Recipe> favRecipes = userAppRepository.getFavRecipes(userApp.getId());
        favRecipes.remove(recipe);
        userApp.setFavouriteRecipes(favRecipes);
        userAppRepository.save(userApp);

    }
}
