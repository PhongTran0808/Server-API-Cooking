package com.cookingapp.server.api;

import com.cookingapp.server.entity.Favorite;
import com.cookingapp.server.entity.Recipe;
import com.cookingapp.server.entity.User;
import com.cookingapp.server.repository.FavoriteRepository;
import com.cookingapp.server.repository.RecipeRepository;
import com.cookingapp.server.repository.UserRepository;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/favorites")
@Tag(name = "Favorites", description = "API quản lý công thức yêu thích")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lấy danh sách yêu thích của người dùng", description = "Lấy tất cả công thức yêu thích của một người dùng")
    public ResponseEntity<Map<String, Object>> getUserFavorites(
            @Parameter(description = "ID người dùng") @PathVariable Long userId,
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size) {

        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Favorite> favoritePage = favoriteRepository.findByUserOrderByCreatedAtDesc(user.get(), pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("favorites", favoritePage.getContent());
        response.put("currentPage", favoritePage.getNumber());
        response.put("totalElements", favoritePage.getTotalElements());
        response.put("totalPages", favoritePage.getTotalPages());
        response.put("pageSize", favoritePage.getSize());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Thêm công thức vào yêu thích", description = "Thêm một công thức vào danh sách yêu thích của người dùng")
    public ResponseEntity<Map<String, Object>> addToFavorites(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long recipeId = request.get("recipeId");
        
        Optional<User> user = userRepository.findById(userId);
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        
        Map<String, Object> response = new HashMap<>();
        
        if (!user.isPresent() || !recipe.isPresent()) {
            response.put("error", "User or Recipe not found");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Check if already favorited
        if (favoriteRepository.existsByUserAndRecipe(user.get(), recipe.get())) {
            response.put("error", "Recipe already in favorites");
            return ResponseEntity.badRequest().body(response);
        }
        
        Favorite favorite = new Favorite(user.get(), recipe.get());
        Favorite savedFavorite = favoriteRepository.save(favorite);
        
        response.put("favorite", savedFavorite);
        response.put("message", "Recipe added to favorites");
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}/recipe/{recipeId}")
    @Operation(summary = "Xóa khỏi yêu thích", description = "Xóa một công thức khỏi danh sách yêu thích")
    public ResponseEntity<Map<String, String>> removeFromFavorites(
            @Parameter(description = "ID người dùng") @PathVariable Long userId,
            @Parameter(description = "ID công thức") @PathVariable Long recipeId) {
        
        Optional<User> user = userRepository.findById(userId);
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        
        if (!user.isPresent() || !recipe.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Favorite> favorite = favoriteRepository.findByUserAndRecipe(user.get(), recipe.get());
        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
            return ResponseEntity.ok(Map.of("message", "Recipe removed from favorites"));
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/recipe/{recipeId}/count")
    @Operation(summary = "Đếm số lượt yêu thích", description = "Đếm số người đã yêu thích một công thức")
    public ResponseEntity<Map<String, Object>> getFavoriteCount(@Parameter(description = "ID công thức") @PathVariable Long recipeId) {
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        
        if (!recipe.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Long count = favoriteRepository.countByRecipe(recipe.get());
        Map<String, Object> response = new HashMap<>();
        response.put("recipeId", recipeId);
        response.put("favoriteCount", count);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    @Operation(summary = "Kiểm tra yêu thích", description = "Kiểm tra xem người dùng đã yêu thích công thức chưa")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @Parameter(description = "ID người dùng") @RequestParam Long userId,
            @Parameter(description = "ID công thức") @RequestParam Long recipeId) {
        
        Optional<User> user = userRepository.findById(userId);
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        
        Map<String, Object> response = new HashMap<>();
        
        if (!user.isPresent() || !recipe.isPresent()) {
            response.put("error", "User or Recipe not found");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean isFavorited = favoriteRepository.existsByUserAndRecipe(user.get(), recipe.get());
        response.put("isFavorited", isFavorited);
        response.put("userId", userId);
        response.put("recipeId", recipeId);
        
        return ResponseEntity.ok(response);
    }
}