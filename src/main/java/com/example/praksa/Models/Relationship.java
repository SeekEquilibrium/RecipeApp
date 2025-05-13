package com.example.praksa.Models;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;



@Getter
@RelationshipProperties
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {
    @Id
    @GeneratedValue
    private  long id;

    @Property("status")
    private int status;

    @Property("createdAt")
    private LocalDateTime createdAt;

    @TargetNode
    private UserNode friend;


}
