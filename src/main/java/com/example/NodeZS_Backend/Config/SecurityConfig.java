package com.example.NodeZS_Backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // This tells Spring to apply this security logic
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for testing REST APIs
                .cors(cors -> cors.disable()) // Disable default CORS to prevent conflicts with your Controller's @CrossOrigin
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // This "opens the gates" for Postman and React
                );
        return http.build();
    }
}