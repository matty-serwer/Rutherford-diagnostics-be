package com.ltde.rutherford_d1.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        Contact contact = new Contact()
                .name("Rutherford Diagnostics Team")
                .email("support@rutherford-diagnostics.com");

        License license = new License()
                .name("Private")
                .url("https://rutherford-diagnostics.com/license");

        Info info = new Info()
                .title("Rutherford Diagnostics API")
                .version("1.0.0")
                .description("API for veterinary diagnostic dashboard")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
} 