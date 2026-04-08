package com.cookingapp.server.api;

import com.cookingapp.server.entity.Category;
import com.cookingapp.server.entity.Recipe;
import com.cookingapp.server.repository.CategoryRepository;
import com.cookingapp.server.repository.RecipeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@Tag(name = "Categories", description = "API quản lý danh mục món ăn")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả danh mục")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết danh mục theo ID")
    public ResponseEntity<?> getCategoryById(
            @Parameter(description = "ID của danh mục") @PathVariable Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Category not found"));
        }
        return ResponseEntity.ok(category.get());
    }

    @GetMapping("/{id}/recipes")
    @Operation(summary = "Lấy danh sách công thức theo danh mục")
    public ResponseEntity<Map<String, Object>> getRecipesByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Recipe> recipePage = recipeRepository.findByCategoryIdOrderByCreatedAtDesc(id, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("recipes", recipePage.getContent());
        response.put("totalElements", recipePage.getTotalElements());
        response.put("totalPages", recipePage.getTotalPages());
        response.put("currentPage", recipePage.getNumber());
        response.put("categoryId", id);
        return ResponseEntity.ok(response);
    }
}
