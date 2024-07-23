package com.example.praksa.Repositories;

import com.example.praksa.DTOs.CountCategoryDTO;
import com.example.praksa.DTOs.CountCategoryProjection;
import com.example.praksa.Models.Recipe;
import com.example.praksa.Models.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe findByName(String name);
    @Query("SELECT us from UserApp us join fetch us.favouriteRecipes fav  where fav.id =?1 and us.id=?2")
    Optional<Recipe> findByRecipeIdAndUserAppId(Long recipeId, Long userAppId);

    @Query("SELECT rec from Recipe rec join fetch rec.recipeCategory cat WHERE cat.name=?1")
    List<Recipe> findAllWithCategory( String categoryName);

    @Query("SELECT rec from Recipe rec join fetch rec.recipeIngredients recIng where recIng.ingredient.name=?1")
    List<Recipe> findAllWithIngredient(String ingredientName);

    @Query("SELECT rec.recipeCategory.name as categoryName , count(rec) as count from Recipe rec group by rec.recipeCategory.name")
    List<CountCategoryProjection> countCategoryAll();

    @Query("SELECT rec.recipeCategory.name as categoryName , count(rec) as count from Recipe rec where rec.recipeCategory.name =?1 group by rec.recipeCategory.name")
    CountCategoryProjection countCategorySpecific(String categoryName);

    Integer countByCreatedDateTimeBetween(LocalDateTime from , LocalDateTime to);

    List<Recipe> findByCreatedDateTimeBetween(LocalDateTime from , LocalDateTime to);
}
