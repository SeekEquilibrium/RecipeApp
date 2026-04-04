package com.example.praksa.Utills;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeCsvRepresentation {
    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "cuisine")
    private String cuisine;

    @CsvBindByName(column = "course")
    private String course;

    @CsvBindByName(column = "diet")
    private String diet;

    @CsvBindByName(column = "ingredients_name")
    private String ingredientsName;

    @CsvBindByName(column = "ingredients_quantity")
    private String ingredientsQuantity;

    @CsvBindByName(column = "prep_time (in mins)")
    private String prepTime;

    @CsvBindByName(column = "cook_time (in mins)")
    private String cookTime;

    @CsvBindByName(column = "instructions")
    private String instructions;

    @CsvBindByName(column = "image_url")
    private String imageUrl;
}