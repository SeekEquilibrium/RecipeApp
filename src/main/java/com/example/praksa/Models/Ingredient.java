package com.example.praksa.Models;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="ingredients")
@Data
public class Ingredient {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String name;
    @Builder
    public Ingredient(String name){
        this.name = name;
    }


}
