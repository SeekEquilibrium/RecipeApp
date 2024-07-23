package com.example.praksa.Converters;

import com.example.praksa.DTOs.FriendResponseDTO;
import com.example.praksa.DTOs.UserDTO;
import com.example.praksa.Models.Adress;
import com.example.praksa.Models.Gender;
import com.example.praksa.Models.UserApp;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDTOConverter {
    private final PasswordEncoder passwordEncoder;


    public UserDTOConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserApp DTOToUser (UserDTO userDTO){
        return  UserApp.builder()
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .phoneNumber(Integer.parseInt(userDTO.getPhoneNumber()))
                .adress(new Adress(userDTO.getStreet(), userDTO.getCity(), userDTO.getCountry()))
                .gender(stringToGender(userDTO.getGender()))
                .build();
    }

    public UserDTO UserToDTO (UserApp userApp){
        return  UserDTO.builder()
                .name(userApp.getName())
                .surname(userApp.getSurname())
                .email(userApp.getEmail())
                .password(userApp.getPassword())
                .phoneNumber(String.valueOf(userApp.getPhoneNumber()))
                .street(userApp.getAdress().getCity())
                .country(userApp.getAdress().getCountry())
                .city(userApp.getAdress().getCity())
                .gender(userApp.getGender().toString())
                .build();
    }

    public FriendResponseDTO userToFriendResponseDTO (UserApp userApp){
        return  FriendResponseDTO.builder()
                .name(userApp.getName())
                .surname(userApp.getSurname())
                .email(userApp.getEmail())
                .build();
    }
    public Gender stringToGender(String input) {
        if(input.trim().equalsIgnoreCase("male")){
            return Gender.MALE;
        }else {
            return Gender.FEMALE;
        }
    }
}
