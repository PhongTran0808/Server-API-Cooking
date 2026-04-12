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
        // Bước 1: Load .env.secrets trước (chứa SECURE_DB_* credentials thực)
        // File này git-ignored, không bao giờ commit lên repo
        // Trong production: thay bằng OS environment variables thực sự
        try {
            Dotenv secrets = Dotenv.configure()
                    .filename(".env.secrets")
                    .ignoreIfMissing()
                    .load();
            secrets.entries().forEach(e -> {
                if (System.getProperty(e.getKey()) == null) {
                    System.setProperty(e.getKey(), e.getValue());
                }
            });
        } catch (DotenvException e) {
            System.out.println("[INFO] No .env.secrets file, using OS environment variables for SECURE_* vars");
        }

        // Bước 2: Load .env chính (chứa DB_* tham chiếu đến SECURE_*)
        // File này có thể được review/commit nhưng KHÔNG chứa credentials
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(e -> {
                if (System.getProperty(e.getKey()) == null) {
                    System.setProperty(e.getKey(), e.getValue());
                }
            });
        } catch (DotenvException e) {
            System.out.println("[INFO] No .env file found, using system environment variables");
        }

        SpringApplication.run(CookingAppServerApplication.class, args);
    }
}