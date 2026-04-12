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
    public void addFavouriteRecipe(String recipeName) throws Exception {
        UserApp principal = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeRepository.findByName(recipeName);
        if (recipe == null) {
            log.error("Recipe not found");
            throw new Exception("Recipe not found");
        }
        if (recipeRepository.findByRecipeIdAndUserAppId(recipe.getId(), principal.getId()).isPresent()) {
            log.error("Recipe already added");
            throw new Exception("Recipe already added");
        }
        UserApp managedUser = userAppRepository.findById(principal.getId())
                .orElseThrow(() -> new Exception("User not found"));
        Set<Recipe> favRecipes = userAppRepository.getFavRecipes(managedUser.getId());
        favRecipes.add(recipe);
        managedUser.setFavouriteRecipes(favRecipes);
        userAppRepository.save(managedUser);
    }
    @Transactional
    public List<RecipeResponseDTO> getFavouritesForUser() {
        UserApp user = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Recipe> favoritesList = userAppRepository.getFavRecipes(user.getId()).stream().toList();
        return favoritesList.stream().map(converter::recipeToDTO).toList();
    }

    @Transactional
    public void deleteFavouriteCategory(String recipeName) throws Exception {
        UserApp principal = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeRepository.findByName(recipeName);
        if (recipe == null) {
            log.error("Recipe not found");
            throw new Exception("Recipe not found");
        }
        if (recipeRepository.findByRecipeIdAndUserAppId(recipe.getId(), principal.getId()).isEmpty()) {
            log.error("Recipe not in favourites");
            throw new Exception("Recipe not in favourites");
        }
        UserApp managedUser = userAppRepository.findById(principal.getId())
                .orElseThrow(() -> new Exception("User not found"));
        Set<Recipe> favRecipes = userAppRepository.getFavRecipes(managedUser.getId());
        favRecipes.remove(recipe);
        managedUser.setFavouriteRecipes(favRecipes);
        userAppRepository.save(managedUser);
    }
}
