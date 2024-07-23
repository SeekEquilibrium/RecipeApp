package com.example.praksa.Models;

import javax.persistence.*;

import lombok.*;
import org.apache.catalina.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="recipeCategory")
public class RecipeCategory {
    @Id
    @SequenceGenerator(name = "categorySeqGen", sequenceName = "categorySeq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "categorySeqGen")
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
