package com.example.praksa.Converters;

import com.example.praksa.DTOs.UserDTO;
import com.example.praksa.Models.Adress;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Models.UserNode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserNodeDTOConverter {
    private final PasswordEncoder passwordEncoder;

    public UserNodeDTOConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserNode DTOToUserNode (UserDTO userDTO){
        return  UserNode.builder()
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .phoneNumber(Integer.parseInt(userDTO.getPhoneNumber()))
                .build();
    }
}
