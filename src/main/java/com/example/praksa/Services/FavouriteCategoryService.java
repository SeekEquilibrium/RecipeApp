package com.example.praksa.Services;

import com.example.praksa.Models.RecipeCategory;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Repositories.postgres.RecipeCategoryRepository;
import com.example.praksa.Repositories.postgres.UserAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Set;

@Service
@Slf4j
public class FavouriteCategoryService {

    private final RecipeCategoryRepository recipeCategoryRepository;

    private final UserAppRepository userAppRepository;

    public FavouriteCategoryService(RecipeCategoryRepository recipeCategoryRepository, UserAppRepository userAppRepository) {
        this.recipeCategoryRepository = recipeCategoryRepository;
        this.userAppRepository = userAppRepository;
    }

    @Transactional
    public void addFavouriteCategory(Long categoryId) throws Exception {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RecipeCategory recipeCategory = recipeCategoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Recipe category not found");
            throw new RuntimeException("Recipe not found");
        });
        if (recipeCategoryRepository.findByCategoryIdAndUserAppId(recipeCategory.getId(),userApp.getId()).isPresent()) {
            log.error("Recipe category already added");
            throw new Exception("Recipe category already added");
        }
        Set<RecipeCategory> favCategories = userAppRepository.getFavRecipeCategories(userApp.getId());
        favCategories.add(recipeCategory);
        userApp.setFavouriteCategories(favCategories);
        userAppRepository.save(userApp);
    }

    public Set<RecipeCategory> getFavouritesForUser() {
        UserApp user = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<RecipeCategory> favoritesList = userAppRepository.getFavRecipeCategories(user.getId());
       return favoritesList;
    }

    public void deleteFavouriteCategory(Long categoryId) throws Exception {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RecipeCategory recipeCategory = recipeCategoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Recipe category not found");
            throw new RuntimeException("Recipe not found");
        });
        if (recipeCategoryRepository.findByCategoryIdAndUserAppId(recipeCategory.getId(),userApp.getId()).isEmpty()) {
            log.error("Recipe category not in favourites");
            throw new Exception("Recipe category not in favourites");
        }
        Set<RecipeCategory> favCategories = userAppRepository.getFavRecipeCategories(userApp.getId());
        favCategories.remove(recipeCategory);
        userApp.setFavouriteCategories(favCategories);
        userAppRepository.save(userApp);

    }
}

