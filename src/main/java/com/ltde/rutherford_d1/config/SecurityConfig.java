package com.ltde.rutherford_d1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration for the application.
 * Handles CORS settings and endpoint authorization.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsProperties corsProperties;

    /**
     * Constructor injection of CORS properties.
     * This allows clean configuration management through application.properties files.
     */
    public SecurityConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    /**
     * Configures the security filter chain with CORS and authorization rules.
     * - Enables CORS for cross-origin requests
     * - Disables CSRF for API-only usage
     * - Permits all requests to /patient/** and /test/** endpoints
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/patient/**", "/test/**").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }

    /**
     * Creates CORS configuration source using properties from application.properties.
     * This method reads the CORS settings from CorsProperties which maps
     * the cors.* properties from configuration files.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins from properties
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        
        // Set allowed methods from properties (with fallback defaults)
        if (corsProperties.getAllowedMethods() != null && !corsProperties.getAllowedMethods().isEmpty()) {
            configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        } else {
            configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        }
        
        // Set allowed headers from properties (with fallback defaults)
        if (corsProperties.getAllowedHeaders() != null && !corsProperties.getAllowedHeaders().isEmpty()) {
            configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        } else {
            configuration.setAllowedHeaders(java.util.Arrays.asList("*"));
        }
        
        // Set allow credentials from properties
        configuration.setAllowCredentials(corsProperties.isAllowCredentials());
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 