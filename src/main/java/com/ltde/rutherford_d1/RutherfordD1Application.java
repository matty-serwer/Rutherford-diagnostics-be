package com.ltde.rutherford_d1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ltde.rutherford_d1.config.CorsProperties;

/**
 * Main application class for Rutherford Diagnostics API.
 * Enables configuration properties mapping for clean property management.
 */
@SpringBootApplication
@EnableConfigurationProperties(CorsProperties.class)
public class RutherfordD1Application {

	public static void main(String[] args) {
		SpringApplication.run(RutherfordD1Application.class, args);
	}

}
