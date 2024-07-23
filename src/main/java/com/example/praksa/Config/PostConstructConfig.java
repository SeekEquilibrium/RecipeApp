package com.example.praksa.Config;

import com.example.praksa.Models.Adress;
import com.example.praksa.Models.Gender;
import com.example.praksa.Models.Role;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Repositories.RoleRepository;
import com.example.praksa.Repositories.UserAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class PostConstructConfig {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAppRepository userAppRepository;

    public PostConstructConfig(RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserAppRepository userAppRepository) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAppRepository = userAppRepository;
    }
    @PostConstruct
    public void create(){
        if(roleRepository.findAll().isEmpty()){
            Role roleUser = new Role("ROLE_USER");
            Role roleAdmin = new Role("ROLE_ADMIN");
            roleRepository.save(roleUser);
            roleRepository.save(roleAdmin);

            UserApp admin1 = new UserApp(1,"Jovan", "Jovanovic","admin@gmail.com", passwordEncoder.encode("admin"),123 , Gender.MALE,new Adress("Grobljanska1","Ruma","Srbija"),true);
            admin1.setRole(roleAdmin);
            userAppRepository.save(admin1);

            UserApp user1 = new UserApp(2,"Nikola", "Slavnic","nikola@gmail.com", passwordEncoder.encode("nikola"),123 , Gender.MALE,new Adress("Grobljanska2","Ruma","Srbija"),true);
            user1.setRole(roleUser);
            userAppRepository.save(user1);
        }


    }
}
