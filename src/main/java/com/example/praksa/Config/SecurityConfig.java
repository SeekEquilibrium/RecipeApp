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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableMethodSecurity
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
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling ->
                        handling.authenticationEntryPoint(restAuthenticationEntryPoint))
                .authorizeHttpRequests(requests -> requests
                        // Public endpoints
                        .requestMatchers(
                                "/auth/**",
                                "/socket/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/recipe/getAll",
                                "/recipe/filter/category",
                                "/ingredient/getAll",
                                // Static resources - safe patterns only
                                "/*.html",
                                "/favicon.ico",
                                "/static/**",
                                "/public/**",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/img/**"
                        ).permitAll()
                        // All other requests need authentication
                        .anyRequest().authenticated())
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(
                        new TokenAuthenticationFilter(tokenHandler, userAppService),
                        BasicAuthenticationFilter.class);

        return http.build();

    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                // Only ignore truly static resources from specific locations
                .requestMatchers(
                        "/webjars/**",
                        "/favicon.ico"
                );
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Option 1: For development (allows credentials)
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*"));
        configuration.setAllowCredentials(true);

        // Option 2: For production without credentials (uncomment if needed)
        // configuration.setAllowedOrigins(List.of("*"));
        // configuration.setAllowCredentials(false);

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "authorization", "content-type", "x-auth-token", "access_token"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
