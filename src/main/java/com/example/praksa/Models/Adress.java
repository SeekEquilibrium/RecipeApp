package com.example.praksa.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Adress {
    @Column
    private String street;
    @Column
    private String city;
    @Column
    private String country;

}
