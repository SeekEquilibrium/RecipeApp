package com.example.praksa.Repositories;

import com.example.praksa.Models.Recipe;
import com.example.praksa.Models.RecipeCategory;
import com.example.praksa.Models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp,Long> {
    UserApp findByEmail(String username);

    @Query("select us.favouriteCategories from UserApp us where us.id =? 1")
    Set<RecipeCategory> getFavRecipeCategories(Long userId);

    @Query("select us.favouriteRecipes from UserApp us where us.id =? 1")
    Set<Recipe> getFavRecipes(Long userId);
}
