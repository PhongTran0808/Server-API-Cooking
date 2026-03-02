package com.cookingapp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Main Spring Boot application class for Cooking App Server
 */
@SpringBootApplication
@CrossOrigin(origins = "*") // Allow CORS for client-server communication
public class CookingAppServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookingAppServerApplication.class, args);
    }
}