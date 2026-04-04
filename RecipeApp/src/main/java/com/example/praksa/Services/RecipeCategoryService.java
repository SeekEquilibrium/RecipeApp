package com.example.praksa.Services;

import com.example.praksa.Models.RecipeCategory;
import com.example.praksa.Repositories.postgres.RecipeCategoryRepository;
import com.example.praksa.Repositories.postgres.UserAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RecipeCategoryService {

    private final RecipeCategoryRepository recipeCategoryRepository;
    private final UserAppRepository userAppRepository;

    public RecipeCategoryService(RecipeCategoryRepository recipeCategoryRepository, UserAppRepository userAppRepository) {
        this.recipeCategoryRepository = recipeCategoryRepository;
        this.userAppRepository = userAppRepository;
    }

    public void deleteRecipeCategory(Long id) throws Exception{
        RecipeCategory recipeCategory = recipeCategoryRepository.findById(id).orElseThrow(()->{
            log.error("Recipe category not found");
            return new Exception("Recipe category not found");
        });
        recipeCategoryRepository.delete(recipeCategory);
    }


}
