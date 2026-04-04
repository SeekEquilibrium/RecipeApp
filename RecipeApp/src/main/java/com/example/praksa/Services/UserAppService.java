package com.example.praksa.Services;

import com.example.praksa.Models.UserApp;
import com.example.praksa.Repositories.postgres.UserAppRepository;
import com.example.praksa.Security.TokenHandler;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAppService implements UserDetailsService {
    private final UserAppRepository userAppRepository;
    private final TokenHandler tokenHandler;
    private final RefreshTokenService refreshTokenService;

    public UserAppService(UserAppRepository userAppRepository, TokenHandler tokenHandler, RefreshTokenService refreshTokenService) {
        this.userAppRepository = userAppRepository;
        this.tokenHandler = tokenHandler;
        this.refreshTokenService = refreshTokenService;
    }


    public UserApp findByEmail(String email) {
        return   userAppRepository.findByEmail(email);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserApp user = userAppRepository.findByEmail(username);
        if(user == null){
            try {
                throw new Exception("User not found");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            return user;
        }
    }
}
