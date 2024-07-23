package com.example.praksa.DTOs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RecipeCategoryDTO {
    private String name;
    private String description;
}
