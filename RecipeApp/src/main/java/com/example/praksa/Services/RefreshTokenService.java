package com.example.praksa.Services;

import com.example.praksa.Models.RefreshToken;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Repositories.postgres.RefreshTokenRepository;
import com.example.praksa.Repositories.postgres.UserAppRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAppRepository userAppRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserAppRepository userAppRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userAppRepository = userAppRepository;
    }
    public Optional<RefreshToken> findByToken (String token){
        return refreshTokenRepository.findByToken(token);
    }
    public RefreshToken createRefreshToken(Long userId){
        Optional<UserApp> user = userAppRepository.findById(userId);
        if(user.isPresent()){
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(user.get())
                    .expireDate(new Date().getTime() + 1000*1000)
                    .token(String.valueOf(new Random().nextLong()))
                    .build();
            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        } else {
            return null;
        }
    }
    @Transactional
    public void deleteByUserId(Long userId) {
        Optional<UserApp> user = userAppRepository.findById(userId);
        user.ifPresent(refreshTokenRepository::deleteByUser);
    }

    public RefreshToken verifyExpiration(RefreshToken token) throws Exception {
        if (token.getExpireDate().compareTo(new Date().getTime()) < 0) {
            refreshTokenRepository.delete(token);
            throw new Exception("Refresh token expired");
        } else {
            return token;
        }
    }


}
