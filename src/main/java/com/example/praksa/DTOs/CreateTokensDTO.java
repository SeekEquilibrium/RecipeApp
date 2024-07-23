package com.example.praksa.DTOs;

import com.example.praksa.Models.RefreshToken;
import com.example.praksa.Models.UserApp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseCookie;

@Getter
@Setter
@AllArgsConstructor
public class CreateTokensDTO {
    private UserApp userApp;
    private String jwt;
    private  ResponseCookie jwtCookie;
    private ResponseCookie jwtRefreshCookie;
}
