package com.example.praksa.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.schema.Relationship;

import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import java.util.HashSet;
import java.util.Set;

@Node("UserNode")
@Data
@NoArgsConstructor
@Builder
public class UserNode {
    @Id
    private String email;

    @Property("name")
    private String name;

    @Property("surname")
    private String surname;

    @Property("password")
    private String password;

    @Property("phoneNumber")
    private Integer phoneNumber;


    public UserNode( String name, String surname, String email, String password, int phoneNumber) {

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
