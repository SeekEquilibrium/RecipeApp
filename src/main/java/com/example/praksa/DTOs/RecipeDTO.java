package com.example.praksa.DTOs;

import com.example.praksa.Models.Ingredient;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data

public class RecipeDTO {
    private String name;
    private String preparation;
    private String servings;
    private String recipeCategory;
    private List<RecipeIngredientRequestDTO> ingredientRequestDTOList;

}
