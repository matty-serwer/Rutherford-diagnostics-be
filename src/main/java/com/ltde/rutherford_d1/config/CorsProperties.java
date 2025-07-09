package com.ltde.rutherford_d1.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Configuration properties for CORS settings.
 * This class maps the cors.* properties from application.properties files
 * to strongly-typed Java configuration.
 */
@Data
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    
    /**
     * List of allowed origins for CORS requests.
     * Can be specified as comma-separated values in properties files.
     * Example: cors.allowed-origins=http://localhost:3000,http://localhost:10000
     */
    private List<String> allowedOrigins;
    
    /**
     * List of allowed HTTP methods for CORS requests.
     * Default: GET,POST,PUT,DELETE,OPTIONS
     */
    private List<String> allowedMethods;
    
    /**
     * List of allowed headers for CORS requests.
     * Default: * (all headers)
     */
    private List<String> allowedHeaders;
    
    /**
     * Whether to allow credentials in CORS requests.
     * Default: true
     */
    private boolean allowCredentials = true;
} 