package com.cookingapp.server.api;

import com.cookingapp.server.entity.Recipe;
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
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
@Tag(name = "Recipes", description = "API quản lý công thức nấu ăn")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả công thức")
    public ResponseEntity<Map<String, Object>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "false") boolean full) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Recipe> recipePage;

        if (difficulty != null && !difficulty.isBlank()) {
            try {
                Recipe.DifficultyLevel level = Recipe.DifficultyLevel.valueOf(difficulty.toUpperCase());
                recipePage = recipeRepository.findByDifficulty(level, pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid difficulty: " + difficulty));
            }
        } else {
            recipePage = recipeRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("recipes", recipePage.getContent());
        response.put("totalElements", recipePage.getTotalElements());
        response.put("totalPages", recipePage.getTotalPages());
        response.put("currentPage", recipePage.getNumber());
        response.put("pageSize", recipePage.getSize());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết công thức theo ID")
    public ResponseEntity<?> getRecipeById(
            @Parameter(description = "ID của công thức") @PathVariable Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Recipe not found"));
        }
        return ResponseEntity.ok(recipe.get());
    }

    @GetMapping("/featured")
    @Operation(summary = "Lấy danh sách công thức nổi bật")
    public ResponseEntity<List<Recipe>> getFeaturedRecipes() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<Recipe> page = recipeRepository.findFeaturedRecipesPaginated(pageable);
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/popular")
    @Operation(summary = "Lấy danh sách công thức phổ biến nhất")
    public ResponseEntity<List<Recipe>> getPopularRecipes(
            @RequestParam(defaultValue = "6") int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Recipe> page = recipeRepository.findFeaturedRecipesPaginated(pageable);
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/recent")
    @Operation(summary = "Lấy danh sách công thức mới nhất")
    public ResponseEntity<List<Recipe>> getRecentRecipes(
            @RequestParam(defaultValue = "6") int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<Recipe> page = recipeRepository.findAll(pageable);
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Lấy công thức theo danh mục")
    public ResponseEntity<Map<String, Object>> getRecipesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Recipe> recipePage = recipeRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("recipes", recipePage.getContent());
        response.put("totalElements", recipePage.getTotalElements());
        response.put("totalPages", recipePage.getTotalPages());
        response.put("currentPage", recipePage.getNumber());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Tạo công thức mới")
    public ResponseEntity<?> createRecipe(@RequestBody Recipe recipe) {
        try {
            Recipe saved = recipeRepository.save(recipe);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create recipe: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật công thức")
    public ResponseEntity<?> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipeData) {
        Optional<Recipe> existing = recipeRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Recipe not found"));
        }
        Recipe recipe = existing.get();
        if (recipeData.getTitle() != null) recipe.setTitle(recipeData.getTitle());
        if (recipeData.getDescription() != null) recipe.setDescription(recipeData.getDescription());
        if (recipeData.getIngredients() != null) recipe.setIngredients(recipeData.getIngredients());
        if (recipeData.getInstructions() != null) recipe.setInstructions(recipeData.getInstructions());
        if (recipeData.getCookingTime() != null) recipe.setCookingTime(recipeData.getCookingTime());
        if (recipeData.getDifficulty() != null) recipe.setDifficulty(recipeData.getDifficulty());
        if (recipeData.getServings() != null) recipe.setServings(recipeData.getServings());
        if (recipeData.getImage() != null) recipe.setImage(recipeData.getImage());
        return ResponseEntity.ok(recipeRepository.save(recipe));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa công thức")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        if (!recipeRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Recipe not found"));
        }
        recipeRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Recipe deleted successfully"));
    }
}
