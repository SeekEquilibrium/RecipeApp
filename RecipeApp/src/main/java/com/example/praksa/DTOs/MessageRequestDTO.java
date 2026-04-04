package com.example.praksa.DTOs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRequestDTO {
    private Long toUserId;
    private String content;
}
