package com.cookingapp.server.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Home controller for serving the main page and basic info
 */
@Controller
public class HomeController {

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
                            <h3>🍽️ Recipes API</h3>
                            <p><strong>GET</strong> <a href="/recipes">/recipes</a> - Danh sách công thức</p>
                            <p><strong>GET</strong> <a href="/recipes/1">/recipes/{id}</a> - Chi tiết công thức</p>
                            <p><strong>GET</strong> <a href="/recipes/featured">/recipes/featured</a> - Công thức nổi bật</p>
                            <p><strong>POST</strong> /recipes - Tạo công thức mới</p>
                            <p><strong>PUT</strong> /recipes/{id} - Cập nhật công thức</p>
                            <p><strong>DELETE</strong> /recipes/{id} - Xóa công thức</p>
                        </div>
                        
                        <div class="endpoint">
                            <h3>📂 Categories API</h3>
                            <p><strong>GET</strong> <a href="/categories">/categories</a> - Danh sách danh mục</p>
                            <p><strong>GET</strong> <a href="/categories/1">/categories/{id}</a> - Chi tiết danh mục</p>
                            <p><strong>GET</strong> <a href="/categories/1/recipes">/categories/{id}/recipes</a> - Công thức theo danh mục</p>
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
                    
                    <h2>📱 Client Applications:</h2>
                    
                    <div class="grid">
                        <div class="endpoint">
                            <h3>🌐 Test Client</h3>
                            <p>Mở file <strong>test-client.html</strong> trong browser để test API</p>
                        </div>
                        
                        <div class="endpoint">
                            <h3>💻 Full Client</h3>
                            <p>Cài đặt Node.js và chạy:</p>
                            <p><code>cd cooking-app-client && npm install && npm start</code></p>
                        </div>
                    </div>
                    
                    <div class="status" style="background: #2196F3; margin-top: 30px;">
                        🎉 Hệ thống client-server architecture đã sẵn sàng!
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    @GetMapping("/info")
    @ResponseBody
    public String info() {
        return """
            {
                "name": "Cooking App REST API Server",
                "version": "1.0.0",
                "status": "running",
                "port": 8081,
                "context": "/api",
                "database": "H2 In-Memory",
                "endpoints": {
                    "recipes": "/recipes",
                    "categories": "/categories",
                    "h2-console": "/h2-console"
                }
            }
            """;
    }
}