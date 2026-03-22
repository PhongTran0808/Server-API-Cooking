package com.cookingapp.server.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * REST API Controller for Recipe operations
 */
@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
@Tag(name = "Recipes", description = "API quản lý công thức nấu ăn")
public class RecipeController {

    @Operation(
        summary = "Lấy danh sách tất cả công thức",
        description = "Trả về danh sách tất cả công thức nấu ăn với phân trang và bộ lọc"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRecipes(
            @Parameter(description = "Số trang (bắt đầu từ 0)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng items per page") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Lọc theo danh mục") 
            @RequestParam(required = false) String category,
            @Parameter(description = "Lọc theo độ khó") 
            @RequestParam(required = false) String difficulty,
            @Parameter(description = "Trả về đầy đủ thông tin bao gồm nguyên liệu và hướng dẫn") 
            @RequestParam(defaultValue = "false") boolean full) {
        
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> recipes = new ArrayList<>();
        
        // Dữ liệu mẫu
        Map<String, Object> recipe1 = new HashMap<>();
        recipe1.put("id", 1);
        recipe1.put("title", "Phở Bò Hà Nội");
        recipe1.put("description", "Món phở bò truyền thống của Hà Nội với nước dùng trong vắt, thơm ngon");
        recipe1.put("image", "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/anh_mon_an.png");
        recipe1.put("cookingTime", 240);
        recipe1.put("difficulty", "MEDIUM");
        recipe1.put("servings", 4);
        recipe1.put("averageRating", 4.8);
        recipe1.put("ratingCount", 24);
        if (full) {
            recipe1.put("ingredients", "- 500g xương bò\n- 300g thịt bò tái\n- 200g bánh phở\n- Hành tây, gừng\n- Gia vị: muối, đường, nước mắm\n- Rau thơm: ngò gai, hành lá");
            recipe1.put("instructions", "1. Ninh xương bò 3-4 tiếng để có nước dùng trong\n2. Thái thịt bò mỏng\n3. Trụng bánh phở\n4. Cho bánh phở vào tô, xếp thịt bò lên trên\n5. Chan nước dùng nóng\n6. Ăn kèm rau thơm và gia vị");
        }
        recipes.add(recipe1);
        
        Map<String, Object> recipe2 = new HashMap<>();
        recipe2.put("id", 2);
        recipe2.put("title", "Bún Chả Hà Nội");
        recipe2.put("description", "Món bún chả đặc trưng của Hà Nội với chả nướng thơm lừng");
        recipe2.put("image", "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/cua.png");
        recipe2.put("cookingTime", 45);
        recipe2.put("difficulty", "EASY");
        recipe2.put("servings", 2);
        recipe2.put("averageRating", 4.5);
        recipe2.put("ratingCount", 18);
        if (full) {
            recipe2.put("ingredients", "- 300g thịt lợn xay\n- 200g bún tươi\n- Nước mắm, đường, tỏi, ớt\n- Rau sống: xà lách, kinh giới, tía tô");
            recipe2.put("instructions", "1. Trộn thịt lợn với gia vị, nặn thành viên\n2. Nướng chả trên than hoa\n3. Pha nước chấm chua ngọt\n4. Trụng bún\n5. Ăn kèm rau sống và nước chấm");
        }
        recipes.add(recipe2);
        
        response.put("recipes", recipes);
        response.put("totalElements", recipes.size());
        response.put("totalPages", 1);
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("full", full);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Lấy chi tiết công thức theo ID",
        description = "Trả về thông tin chi tiết của một công thức nấu ăn"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy công thức")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRecipeById(
            @Parameter(description = "ID của công thức", example = "1") 
            @PathVariable Long id) {
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("id", id);
        recipe.put("title", "Phở Bò Hà Nội");
        recipe.put("description", "Món phở bò truyền thống của Hà Nội với nước dùng trong vắt, thơm ngon");
        recipe.put("ingredients", "- 500g xương bò\n- 300g thịt bò tái\n- 200g bánh phở\n- Hành tây, gừng\n- Gia vị: muối, đường, nước mắm\n- Rau thơm: ngò gai, hành lá");
        recipe.put("instructions", "1. Ninh xương bò 3-4 tiếng để có nước dùng trong\n2. Thái thịt bò mỏng\n3. Trụng bánh phở\n4. Cho bánh phở vào tô, xếp thịt bò lên trên\n5. Chan nước dùng nóng\n6. Ăn kèm rau thơm và gia vị");
        recipe.put("image", "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/anh_mon_an.png");
        recipe.put("cookingTime", 240);
        recipe.put("difficulty", "MEDIUM");
        recipe.put("servings", 4);
        recipe.put("averageRating", 4.8);
        recipe.put("ratingCount", 24);
        recipe.put("viewCount", 1250);
        
        // Initialize comments collection to prevent LazyInitializationException
        // This triggers lazy loading while session is still open
        List<Map<String, Object>> comments = new ArrayList<>();
        recipe.put("comments", comments);
        recipe.put("commentCount", 0);
        
        return ResponseEntity.ok(recipe);
    }

    @Operation(
        summary = "Lấy tất cả công thức đầy đủ thông tin",
        description = "Trả về tất cả công thức bao gồm nguyên liệu và hướng dẫn nấu"
    )
    @GetMapping("/full")
    public ResponseEntity<Map<String, Object>> getAllRecipesFull(
            @Parameter(description = "Số trang (bắt đầu từ 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng items per page")
            @RequestParam(defaultValue = "10") int size) {
        // Gọi lại getAllRecipes với full=true
        return getAllRecipes(page, size, null, null, true);
    }

    @Operation(
        summary = "Lấy danh sách công thức nổi bật",
        description = "Trả về danh sách các công thức được đánh giá cao"
    )
    @GetMapping("/featured")
    public ResponseEntity<List<Map<String, Object>>> getFeaturedRecipes() {
        List<Map<String, Object>> recipes = new ArrayList<>();
        
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("id", 1);
        recipe.put("title", "Phở Bò Hà Nội");
        recipe.put("description", "Món phở bò truyền thống của Hà Nội");
        recipe.put("image", "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/anh_mon_an.png");
        recipe.put("cookingTime", 240);
        recipe.put("difficulty", "MEDIUM");
        recipe.put("averageRating", 4.8);
        recipes.add(recipe);
        
        return ResponseEntity.ok(recipes);
    }

    @Operation(
        summary = "Tạo công thức mới",
        description = "Tạo một công thức nấu ăn mới"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRecipe(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Thông tin công thức mới",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "title": "Tên món ăn",
                            "description": "Mô tả món ăn",
                            "ingredients": "Danh sách nguyên liệu",
                            "instructions": "Hướng dẫn nấu",
                            "cookingTime": 60,
                            "difficulty": "EASY",
                            "servings": 2
                        }
                        """)
                )
            )
            @RequestBody Map<String, Object> recipeData) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", System.currentTimeMillis());
        response.put("message", "Recipe created successfully");
        response.put("status", "success");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Cập nhật công thức",
        description = "Cập nhật thông tin của một công thức hiện có"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRecipe(
            @Parameter(description = "ID của công thức cần cập nhật") 
            @PathVariable Long id, 
            @RequestBody Map<String, Object> recipeData) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("message", "Recipe updated successfully");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Xóa công thức",
        description = "Xóa một công thức nấu ăn"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRecipe(
            @Parameter(description = "ID của công thức cần xóa") 
            @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Recipe deleted successfully");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
}