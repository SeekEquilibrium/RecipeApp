package com.example.praksa.Models;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    @Id
    @SequenceGenerator(name = "messageGen", sequenceName = "messageSeq",initialValue = 1,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, targetEntity = UserApp.class)
    @JoinColumn(name = "from_user_id", referencedColumnName = "id")
    private UserApp fromUser;

    @ManyToOne(optional = false, targetEntity = UserApp.class)
    @JoinColumn(name = "to_user_id", referencedColumnName = "id")
    private UserApp toUser;

    //@ManyToOne(optional = false, targetEntity = Relationship.class)
    //@JoinColumn(name = "relationship_id", referencedColumnName = "id")
   // private Relationship relationship;

    private String subject;
    private String content;
    private int status;
    private LocalDateTime time;

}
