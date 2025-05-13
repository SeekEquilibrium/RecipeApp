package com.example.praksa.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "userApp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserApp implements UserDetails {
    @Id
    @SequenceGenerator(name = "userAppSeqGen", sequenceName = "userAppSeq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "userAppSeqGen")
    @Column(name="id",  nullable=false)
    private  long id;
    @Column
    private String name;
    @Column
    private String surname;
    @Column(unique = true)
    private String email;
    @Column
    private String password;
    @Column
    private int phoneNumber;
    @Column
    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    @Embedded
    private Adress adress;
    @Column
    private boolean isRegistered;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "FAV_CATEGORIES",
            joinColumns = {@JoinColumn(name = "userApp_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "recipeCategory_id", referencedColumnName = "id")}
    )
    private Set<RecipeCategory> favouriteCategories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "FAV_RECIPES",
            joinColumns = {@JoinColumn(name = "userApp_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "recipe_id", referencedColumnName = "id")}
    )
    private Set<Recipe> favouriteRecipes = new HashSet<>();

   

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL)
    private List<Message> fromUserMessagesList;

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL)
    private List<Message> toUserMessagesList;



    @Builder
    public UserApp(String name, String surname, String email, String password, int phoneNumber, Gender gender, Adress adress) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.adress = adress;
    }

    public UserApp(long id, String name, String surname, String email, String password, int phoneNumber, Gender gender, Adress adress, boolean isRegistered) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.adress = adress;
        this.isRegistered = isRegistered;
    }


    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isRegistered;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isRegistered;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isRegistered;
    }

    @Override
    public boolean isEnabled() {
        return isRegistered;
    }

}
