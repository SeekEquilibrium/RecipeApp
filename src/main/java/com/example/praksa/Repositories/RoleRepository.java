package com.example.praksa.Repositories;

import com.example.praksa.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long > {
    Role findByNameIgnoreCase(String name);
}
