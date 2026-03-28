package com.example.NodeZS_Backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // CRITICAL: This allows the HttpSecurity bean to be found
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API testing
                .cors(cors -> cors.disable()) // Disable default CORS to avoid conflicts with @CrossOrigin
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Requirement: All endpoints accessible for now
                );

        return http.build();
    }
}