package com.example.praksa.Repositories.postgres;

import com.example.praksa.Models.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory,Long> {
    RecipeCategory getByName(String name);
    boolean existsByName(String name);

    @Query("SELECT us from UserApp us join fetch us.favouriteCategories fav  where fav.id =:categoryId and us.id=:userAppId ")
    Optional<RecipeCategory> findByCategoryIdAndUserAppId(Long categoryId, Long userAppId);
    Optional<RecipeCategory> findByName(String name);


}
