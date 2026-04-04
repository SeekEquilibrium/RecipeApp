package com.example.praksa.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private String name;
    private String surname;
    private String email;
    private String password;
    private String phoneNumber;
    private String gender;
    private String street;
    private String city;
    private String country;



}
