package com.example.praksa.Repositories.postgres;

import com.example.praksa.Models.RefreshToken;
import com.example.praksa.Models.UserApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    @Modifying
    int deleteByUser(UserApp user);
}
