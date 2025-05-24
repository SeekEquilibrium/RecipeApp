package com.example.praksa.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class RecipeIngredient {
    @Id
    @SequenceGenerator(name = "recipeIngredientSeqGen", sequenceName = "recipeIngredientSeq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Long id;

    @Column(nullable = false)
    private String amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "ingredient_id")
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "recipe_id")
    private Recipe recipe;

    public RecipeIngredient(String amount, Ingredient ingredient, Recipe recipe) {
        this.amount = amount;
        this.ingredient = ingredient;
        this.recipe = recipe;
    }
}
