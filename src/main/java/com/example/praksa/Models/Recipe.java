package com.example.praksa.Models;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recipe")
public class Recipe {
    @Id
    @SequenceGenerator(name = "recipeGen", sequenceName = "recipeSeq",initialValue = 1,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "recipeSeqGen")
    private long id;
    @Column(unique = true)
    private String name;
    @Column
    private String preparation;
    @Column
    private String servings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeCategory_id")
    private RecipeCategory recipeCategory;

    @OneToOne
    @JoinColumn(name ="image_id")
    private Image image ;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipeIngredient_id")
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    @Column
    private LocalDateTime createdDateTime;
    public void addRecipeIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredients.add(recipeIngredient);
        recipeIngredient.setRecipe(this);
    }

    public void removeRecipeIngredient(RecipeIngredient recipeIngredient){
        recipeIngredients.remove(recipeIngredient);
        recipeIngredient.setRecipe(null);
    }

    public Recipe(String name, String preparation, String servings, RecipeCategory recipeCategory, Image image, List<RecipeIngredient> recipeIngredients) {
        this.name = name;
        this.preparation = preparation;
        this.servings = servings;
        this.recipeCategory = recipeCategory;
        this.image = image;
        this.recipeIngredients = recipeIngredients;
    }
}
