package com.example.praksa.Config;

import com.example.praksa.Models.RecipeCSV;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class RecipeLoader {
   /* private List<RecipeCSV> recipes = new ArrayList<>();

    @PostConstruct
    public void load() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("recipes.csv"))) {
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                RecipeCSV r = new RecipeCSV();
                r.setName(parts[0].trim());

                r.setName(parts[0].trim());
                r.setCookTime(parts[1].trim());
                r.setPrepTime(parts[2].trim());
                r.setRecipeCategory(parts[3].trim());

                r.setKeywords(Arrays.stream(parts[4].replace("\"", "").split(","))
                        .map(String::trim).toList());

                r.setIngredientQuantities(Arrays.stream(parts[5].replace("\"", "").split(","))
                        .map(String::trim).toList());

                r.setIngredientParts(Arrays.stream(parts[6].replace("\"", "").split(","))
                        .map(String::trim).toList());

                r.setCalories(Integer.parseInt(parts[7].trim()));
                r.setFatContent(parts[8].trim());
                r.setCarbohydrateContent(parts[9].trim());
                r.setFiberContent(parts[10].trim());
                r.setSugarContent(parts[11].trim());
                r.setProteinContent(parts[12].trim());

                r.setServings(Integer.parseInt(parts[13].trim()));
                r.setInstructions(parts[14].trim());

                recipes.add(r);
            }
        }
    }

    public List<RecipeCSV> getAll() {
        return recipes;
    }

*/
}
