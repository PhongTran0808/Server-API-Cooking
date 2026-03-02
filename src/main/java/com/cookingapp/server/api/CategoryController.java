package com.cookingapp.server.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * REST API Controller for Category operations
 */
@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*")
@Tag(name = "Categories", description = "API quản lý danh mục món ăn")
public class CategoryController {

    @Operation(
        summary = "Lấy danh sách tất cả danh mục",
        description = "Trả về danh sách tất cả danh mục món ăn"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 1);
        category1.put("name", "Món chính");
        category1.put("description", "Các món ăn chính trong bữa cơm");
        category1.put("image", "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/anh_mon_an.png");
        category1.put("recipeCount", 25);
        categories.add(category1);
        
        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", 2);
        category2.put("name", "Món tráng miệng");
        category2.put("description", "Các món tráng miệng ngọt ngào");
        category2.put("image", "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/cua.png");
        category2.put("recipeCount", 18);
        categories.add(category2);
        
        Map<String, Object> category3 = new HashMap<>();
        category3.put("id", 3);
        category3.put("name", "Món khai vị");
        category3.put("description", "Các món khai vị nhẹ nhàng");
        category3.put("image", "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/anh_mon_an.png");
        category3.put("recipeCount", 12);
        categories.add(category3);
        
        return ResponseEntity.ok(categories);
    }

    @Operation(
        summary = "Lấy chi tiết danh mục theo ID",
        description = "Trả về thông tin chi tiết của một danh mục"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(
            @Parameter(description = "ID của danh mục", example = "1") 
            @PathVariable Long id) {
        Map<String, Object> category = new HashMap<>();
        category.put("id", id);
        category.put("name", "Món chính");
        category.put("description", "Các món ăn chính trong bữa cơm");
        category.put("image", "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/anh_mon_an.png");
        category.put("recipeCount", 25);
        
        return ResponseEntity.ok(category);
    }

    @Operation(
        summary = "Lấy danh sách công thức theo danh mục",
        description = "Trả về danh sách các công thức thuộc một danh mục cụ thể"
    )
    @GetMapping("/{id}/recipes")
    public ResponseEntity<Map<String, Object>> getRecipesByCategory(
            @Parameter(description = "ID của danh mục") 
            @PathVariable Long id,
            @Parameter(description = "Số trang (bắt đầu từ 0)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng items per page") 
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> recipes = new ArrayList<>();
        
        // Sample recipes for the category
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("id", 1);
        recipe.put("title", "Phở Bò Hà Nội");
        recipe.put("description", "Món phở bò truyền thống");
        recipe.put("image", "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/anh_mon_an.png");
        recipe.put("cookingTime", 240);
        recipe.put("difficulty", "MEDIUM");
        recipe.put("averageRating", 4.8);
        recipes.add(recipe);
        
        response.put("recipes", recipes);
        response.put("totalElements", recipes.size());
        response.put("totalPages", 1);
        response.put("currentPage", page);
        response.put("categoryId", id);
        
        return ResponseEntity.ok(response);
    }
}