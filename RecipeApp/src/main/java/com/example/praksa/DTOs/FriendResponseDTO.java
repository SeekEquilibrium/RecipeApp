package com.example.praksa.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendResponseDTO {
    private String name;
    private String surname;
    private String email;
}
