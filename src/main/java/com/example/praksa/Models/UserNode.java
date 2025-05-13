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

@Node("User")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNode {
    @Id
    @SequenceGenerator(name = "userAppSeqGen", sequenceName = "userAppSeq", initialValue = 1, allocationSize = 1)
    @javax.persistence.GeneratedValue(strategy = GenerationType.IDENTITY, generator = "userAppSeqGen")
    private long id;

    @Property("Name")
    private String name;

    @Property("Surname")
    private String surname;

    @Property("email")
    private String email;

    @Property("password")
    private String password;

    @Property("Phone number")
    private int phoneNumber;

    @Relationship(type = "FRIENDS_WITH",direction = Relationship.Direction.OUTGOING)
    private Set<UserNode> friends = new HashSet<>();


    public UserNode(long id, String name, String surname, String email, String password, int phoneNumber) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
