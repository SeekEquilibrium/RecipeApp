package com.example.praksa.Models;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "ROLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role  implements GrantedAuthority {
    @Id
    @SequenceGenerator(name = "RoleSeqGen", sequenceName = "RoleSeq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "RoleSeqGen")
    Long id;
    @Column(name="name")
    String name;

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return this.name;
    }
}
