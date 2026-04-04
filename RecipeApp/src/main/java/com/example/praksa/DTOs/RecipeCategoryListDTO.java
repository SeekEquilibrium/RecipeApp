package com.example.praksa.DTOs;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecipeCategoryListDTO {
    List<RecipeCategoryDTO> recipeCategoryDTOList;
}
