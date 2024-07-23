package com.example.praksa.Config;


import com.example.praksa.Security.RestAuthenticationEntryPoint;
import com.example.praksa.Security.TokenAuthenticationFilter;
import com.example.praksa.Security.TokenHandler;
import com.example.praksa.Services.UserAppService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {
    private final UserAppService userAppService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final TokenHandler tokenHandler;


    public SecurityConfig(UserAppService userAppService, RestAuthenticationEntryPoint restAuthenticationEntryPoint, TokenHandler tokenHandler) {
        this.userAppService = userAppService;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.tokenHandler = tokenHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userAppService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()
                .authorizeRequests()
                .antMatchers("/auth/**",
                        "/socket/**",
                        "/swagger-ui","/swagger-ui.html","/swagger-ui/**" , "/swagger-resources","/swagger-resources/**", "/v2/api-docs" +
                        "/recipe/getAll",
                        "/recipe/filter/category",
                        "/ingredient/getAll").permitAll()
                        .anyRequest().authenticated().and()
                .cors().and()
                .addFilterBefore(new TokenAuthenticationFilter(tokenHandler, userAppService), BasicAuthenticationFilter.class);
        http.csrf().disable();
        return http.build();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(HttpMethod.POST, "/auth/login").and().ignoring().antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "favicon.ico",
                "/swagger-ui","/socket/**","/swagger-ui.html","/swagger-ui/**" , "/swagger-resources","/configuration/ui","/configuration/security","/configuration/**","/swagger-resources/**", "/v2/api-docs", "/**/*.html", "/**/*.css", "/**/*.js","/png/**","/jpg/**","/**/*.jpg","/**/*.png");
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
