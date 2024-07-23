package com.example.praksa.DTOs;

import com.example.praksa.Models.Adress;
import com.example.praksa.Models.Gender;
import com.example.praksa.Models.UserApp;
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
