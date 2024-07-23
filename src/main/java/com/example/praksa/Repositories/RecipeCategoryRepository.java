package com.example.praksa.Repositories;

import com.example.praksa.Models.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory,Long> {
    RecipeCategory getByName(String name);
    boolean existsByName(String name);

    @Query("SELECT us from UserApp us join fetch us.favouriteCategories fav  where fav.id =?1 and us.id=?2 ")
    Optional<RecipeCategory> findByCategoryIdAndUserAppId(Long categoryId, Long userAppId);
    Optional<RecipeCategory> findByName(String name);


}
