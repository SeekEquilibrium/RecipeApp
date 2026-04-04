package com.example.praksa.Repositories.postgres;

import com.example.praksa.Models.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient,Long> {
    boolean existsByNameIgnoreCase(String name);
    Ingredient findByName(String name);
}
