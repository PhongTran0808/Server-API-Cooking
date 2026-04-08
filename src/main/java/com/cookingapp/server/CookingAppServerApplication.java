package com.cookingapp.server;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Main Spring Boot application class for Cooking App Server
 */
@SpringBootApplication
@CrossOrigin(origins = "*")
public class CookingAppServerApplication {

    public static void main(String[] args) {
        // Load .env file if present
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(e -> {
                if (System.getProperty(e.getKey()) == null) {
                    System.setProperty(e.getKey(), e.getValue());
                }
            });
        } catch (DotenvException e) {
            System.out.println("No .env file found, using system environment variables");
        }
        SpringApplication.run(CookingAppServerApplication.class, args);
    }
}