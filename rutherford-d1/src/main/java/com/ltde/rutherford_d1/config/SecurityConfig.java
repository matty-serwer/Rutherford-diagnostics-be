package com.ltde.rutherford_d1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // TODO: Add JWT authentication
                // For now, allow all requests
                .anyRequest().permitAll()
            );
        
        return http.build();
    }

    // TODO: Add these beans when implementing JWT
    // - JwtAuthenticationFilter
    // - AuthenticationProvider
    // - AuthenticationManager
    // - PasswordEncoder
} 