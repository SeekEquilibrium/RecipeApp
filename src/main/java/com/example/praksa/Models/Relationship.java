package com.example.praksa.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "relationship")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {
    @Id
    @SequenceGenerator(name = "relationshipSeqGen", sequenceName = "relationshipSeq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "relationshipSeqGen")
    @Column(name="id", unique=true, nullable=false)
    private  long id;

    @ManyToOne(optional = false, targetEntity = UserApp.class)
    @JoinColumn(name = "userApp_one_id", referencedColumnName = "id" )
    private UserApp userOne;

    @ManyToOne(optional = false, targetEntity = UserApp.class)
    @JoinColumn(name = "userApp_two_id", referencedColumnName = "id" )
    private UserApp userTwo;

    private int status;
    private LocalDateTime time;
    @OneToMany(mappedBy = "relationship", targetEntity = Message.class, cascade = CascadeType.ALL)
    private List<Message> messageList;

    @ManyToOne(optional = false, targetEntity = UserApp.class)
    @JoinColumn(name = "action_user_id", referencedColumnName = "id")
    private UserApp actionUser;


}
