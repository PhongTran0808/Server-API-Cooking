package com.cookingapp.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration for API documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cookingAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🍳 Cooking App REST API")
                        .description("REST API cho ứng dụng hướng dẫn nấu ăn với đầy đủ chức năng quản lý công thức và danh mục món ăn")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081/api")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.cookingapp.com")
                                .description("Production Server")
                ));
    }
}