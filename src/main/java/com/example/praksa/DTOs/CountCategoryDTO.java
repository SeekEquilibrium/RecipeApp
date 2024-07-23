package com.example.praksa.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CountCategoryDTO {
    private String categoryName;
    private Long count;
}
