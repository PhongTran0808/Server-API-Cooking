package com.cookingapp.server.api;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Home controller for serving the main page and basic info
 */
@RestController
public class HomeController implements ErrorController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>🍳 Cooking App Server</title>
                <style>
                    body { 
                        font-family: Arial, sans-serif; 
                        max-width: 800px; 
                        margin: 50px auto; 
                        padding: 20px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                    }
                    .container { 
                        background: rgba(255,255,255,0.1); 
                        padding: 30px; 
                        border-radius: 15px;
                        backdrop-filter: blur(10px);
                    }
                    h1 { color: #fff; text-align: center; }
                    .status { 
                        background: #4CAF50; 
                        padding: 10px; 
                        border-radius: 5px; 
                        margin: 10px 0;
                        text-align: center;
                    }
                    .endpoint { 
                        background: rgba(255,255,255,0.2); 
                        padding: 15px; 
                        margin: 10px 0; 
                        border-radius: 8px;
                        border-left: 4px solid #4CAF50;
                    }
                    .endpoint h3 { margin-top: 0; color: #fff; }
                    a { color: #FFD700; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                    .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
                    @media (max-width: 600px) { .grid { grid-template-columns: 1fr; } }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🍳 Cooking App REST API Server</h1>
                    
                    <div class="status">
                        ✅ Server đang chạy thành công trên port 8081!
                    </div>
                    
                    <h2>📋 API Endpoints Có Sẵn:</h2>
                    
                    <div class="grid">
                        <div class="endpoint">
                            <h3>👤 Users API</h3>
                            <p><strong>GET</strong> <a href="/api/users">/api/users</a> - Danh sách người dùng</p>
                            <p><strong>POST</strong> /api/users/register - Đăng ký</p>
                            <p><strong>POST</strong> /api/users/login - Đăng nhập</p>
                            <p><strong>GET</strong> <a href="/api/users/1">/api/users/{id}</a> - Chi tiết người dùng</p>
                        </div>
                        
                        <div class="endpoint">
                            <h3>🍽️ Recipes API</h3>
                            <p><strong>GET</strong> <a href="/api/recipes">/api/recipes</a> - Danh sách công thức</p>
                            <p><strong>GET</strong> <a href="/api/recipes/1">/api/recipes/{id}</a> - Chi tiết công thức</p>
                            <p><strong>GET</strong> <a href="/api/recipes/featured">/api/recipes/featured</a> - Công thức nổi bật</p>
                            <p><strong>POST</strong> /api/recipes - Tạo công thức mới</p>
                        </div>
                        
                        <div class="endpoint">
                            <h3>📂 Categories API</h3>
                            <p><strong>GET</strong> <a href="/api/categories">/api/categories</a> - Danh sách danh mục</p>
                            <p><strong>GET</strong> <a href="/api/categories/1">/api/categories/{id}</a> - Chi tiết danh mục</p>
                            <p><strong>GET</strong> <a href="/api/categories/1/recipes">/api/categories/{id}/recipes</a> - Công thức theo danh mục</p>
                        </div>
                        
                        <div class="endpoint">
                            <h3>🔍 Search API</h3>
                            <p><strong>GET</strong> <a href="/api/search?q=chicken">/api/search?q=chicken</a> - Tìm kiếm công thức</p>
                            <p><strong>GET</strong> <a href="/api/search/suggestions?q=ch">/api/search/suggestions?q=ch</a> - Gợi ý tìm kiếm</p>
                        </div>
                    </div>
                    
                    <h2>🛠️ Công cụ phát triển:</h2>
                    
                    <div class="grid">
                        <div class="endpoint">
                            <h3>📚 Swagger UI</h3>
                            <p><a href="/swagger-ui.html" target="_blank">Truy cập Swagger UI</a> để test API</p>
                            <p><small>Giao diện tương tác để test tất cả API endpoints</small></p>
                        </div>
                        
                        <div class="endpoint">
                            <h3>💾 H2 Database Console</h3>
                            <p><a href="/h2-console" target="_blank">Truy cập H2 Console</a> để xem database</p>
                            <p><small>JDBC URL: jdbc:h2:mem:cooking_app | User: sa | Password: (để trống)</small></p>
                        </div>
                    </div>
                    
                    <div class="status" style="background: #2196F3; margin-top: 30px;">
                        🎉 Hệ thống REST API đã sẵn sàng!
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    @GetMapping("/api")
    @ResponseBody
    public Map<String, Object> apiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Cooking App REST API Server");
        info.put("version", "1.0.0");
        info.put("status", "running");
        info.put("port", 8081);
        info.put("database", "H2 In-Memory");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("users", "/api/users");
        endpoints.put("recipes", "/api/recipes");
        endpoints.put("categories", "/api/categories");
        endpoints.put("search", "/api/search");
        endpoints.put("swagger-ui", "/swagger-ui.html");
        endpoints.put("h2-console", "/h2-console");
        
        info.put("endpoints", endpoints);
        return info;
    }

    @GetMapping("/info")
    @ResponseBody
    public Map<String, Object> info() {
        return apiInfo();
    }

    @GetMapping("/health")
    @ResponseBody
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "cooking-app-server");
        return health;
    }

    // Error handling
    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Map<String, Object> errorResponse = new HashMap<>();
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorResponse.put("error", "Endpoint không tồn tại");
                errorResponse.put("message", "API endpoint bạn đang tìm không tồn tại. Vui lòng kiểm tra lại URL.");
                errorResponse.put("status", 404);
                errorResponse.put("availableEndpoints", Map.of(
                    "home", "/",
                    "api-info", "/api",
                    "users", "/api/users",
                    "recipes", "/api/recipes",
                    "categories", "/api/categories",
                    "search", "/api/search",
                    "swagger-ui", "/swagger-ui.html"
                ));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorResponse.put("error", "Lỗi server");
                errorResponse.put("message", "Đã xảy ra lỗi server. Vui lòng thử lại sau.");
                errorResponse.put("status", 500);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }
        
        errorResponse.put("error", "Lỗi không xác định");
        errorResponse.put("message", "Đã xảy ra lỗi không xác định.");
        errorResponse.put("status", 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}