package com.cookingapp.server.api;

import com.cookingapp.server.entity.Ingredient;
import com.cookingapp.server.repository.IngredientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/ingredients")
@Tag(name = "Ingredients", description = "API quản lý nguyên liệu")
public class IngredientController {

    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping
    @Operation(summary = "Lấy danh sách nguyên liệu", description = "Lấy tất cả nguyên liệu với phân trang")
    public ResponseEntity<Map<String, Object>> getAllIngredients(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Ingredient> ingredientPage = ingredientRepository.findAllOrderByName(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("ingredients", ingredientPage.getContent());
        response.put("currentPage", ingredientPage.getNumber());
        response.put("totalElements", ingredientPage.getTotalElements());
        response.put("totalPages", ingredientPage.getTotalPages());
        response.put("pageSize", ingredientPage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin nguyên liệu", description = "Lấy thông tin chi tiết nguyên liệu theo ID")
    public ResponseEntity<Ingredient> getIngredientById(@Parameter(description = "ID nguyên liệu") @PathVariable Long id) {
        Optional<Ingredient> ingredient = ingredientRepository.findById(id);
        return ingredient.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm nguyên liệu", description = "Tìm kiếm nguyên liệu theo tên")
    public ResponseEntity<Map<String, Object>> searchIngredients(
            @Parameter(description = "Tên nguyên liệu") @RequestParam String name,
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Ingredient> ingredientPage = ingredientRepository.findByNameContainingIgnoreCase(name, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("ingredients", ingredientPage.getContent());
        response.put("currentPage", ingredientPage.getNumber());
        response.put("totalElements", ingredientPage.getTotalElements());
        response.put("totalPages", ingredientPage.getTotalPages());
        response.put("pageSize", ingredientPage.getSize());
        response.put("searchTerm", name);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Lấy nguyên liệu theo danh mục", description = "Lấy danh sách nguyên liệu theo danh mục")
    public ResponseEntity<List<Ingredient>> getIngredientsByCategory(
            @Parameter(description = "Danh mục nguyên liệu") @PathVariable String category) {
        
        try {
            Ingredient.IngredientCategory ingredientCategory = Ingredient.IngredientCategory.valueOf(category.toUpperCase());
            List<Ingredient> ingredients = ingredientRepository.findByCategoryOrderByName(ingredientCategory);
            return ResponseEntity.ok(ingredients);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    @Operation(summary = "Tạo nguyên liệu mới", description = "Thêm nguyên liệu mới vào hệ thống")
    public ResponseEntity<Map<String, Object>> createIngredient(@RequestBody Ingredient ingredient) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if ingredient name already exists
        if (ingredientRepository.existsByNameIgnoreCase(ingredient.getName())) {
            response.put("error", "Ingredient with this name already exists");
            return ResponseEntity.badRequest().body(response);
        }

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        response.put("ingredient", savedIngredient);
        response.put("message", "Ingredient created successfully");
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật nguyên liệu", description = "Cập nhật thông tin nguyên liệu")
    public ResponseEntity<Ingredient> updateIngredient(@Parameter(description = "ID nguyên liệu") @PathVariable Long id, @RequestBody Ingredient ingredientDetails) {
        Optional<Ingredient> optionalIngredient = ingredientRepository.findById(id);
        
        if (optionalIngredient.isPresent()) {
            Ingredient ingredient = optionalIngredient.get();
            ingredient.setName(ingredientDetails.getName());
            ingredient.setDescription(ingredientDetails.getDescription());
            ingredient.setUnit(ingredientDetails.getUnit());
            ingredient.setImage(ingredientDetails.getImage());
            ingredient.setCategory(ingredientDetails.getCategory());
            
            Ingredient updatedIngredient = ingredientRepository.save(ingredient);
            return ResponseEntity.ok(updatedIngredient);
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa nguyên liệu", description = "Xóa nguyên liệu khỏi hệ thống")
    public ResponseEntity<Map<String, String>> deleteIngredient(@Parameter(description = "ID nguyên liệu") @PathVariable Long id) {
        if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Ingredient deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/categories")
    @Operation(summary = "Lấy danh sách danh mục", description = "Lấy tất cả danh mục nguyên liệu có sẵn")
    public ResponseEntity<Map<String, Object>> getCategories() {
        Map<String, Object> response = new HashMap<>();
        response.put("categories", Ingredient.IngredientCategory.values());
        return ResponseEntity.ok(response);
    }
}