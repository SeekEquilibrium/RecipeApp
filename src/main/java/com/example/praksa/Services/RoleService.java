package com.example.praksa.Services;

import com.example.praksa.Models.Role;
import com.example.praksa.Repositories.postgres.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    public Role findById(Long id){
        Optional<Role> role = roleRepository.findById(id);
        return  role.orElse(null);
    }
    public Role findByName (String name){
        return  roleRepository.findByNameIgnoreCase(name);
    }
}
