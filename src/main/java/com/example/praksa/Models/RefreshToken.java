package com.example.praksa.Models;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "refreshTokens")
public class RefreshToken {
    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserApp user;

    @Id
    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long expireDate;
}
