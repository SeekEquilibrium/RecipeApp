package com.example.praksa.DTOs;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MessageDTO {
    private Long id;
    private Long toUserId;
    private Long fromUserId;
    private String content;
    private String time;

}
