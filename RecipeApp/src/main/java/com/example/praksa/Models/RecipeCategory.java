package com.example.praksa.Models;

import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="recipeCategory")
public class RecipeCategory {
    @Id
    @SequenceGenerator(name = "categorySeqGen", sequenceName = "categorySeq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private long id;
    @Column(unique = true)
    private String name;
    @Column
    private String description;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="recipe_id")
    private Set<Recipe> recipes = new HashSet<>();


    @Builder
    public RecipeCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }



}
