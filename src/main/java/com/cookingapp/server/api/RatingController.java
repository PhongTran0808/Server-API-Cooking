package com.cookingapp.server.api;

import com.cookingapp.server.entity.Rating;
import com.cookingapp.server.entity.Recipe;
import com.cookingapp.server.entity.User;
import com.cookingapp.server.repository.RatingRepository;
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
@RequestMapping("/ratings")
@Tag(name = "Ratings", description = "API quản lý đánh giá và bình luận")
public class RatingController {

    @Autowired
    private RatingRepository ratingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping("/recipe/{recipeId}")
    @Operation(summary = "Lấy đánh giá của công thức", description = "Lấy tất cả đánh giá của một công thức")
    public ResponseEntity<Map<String, Object>> getRecipeRatings(
            @Parameter(description = "ID công thức") @PathVariable Long recipeId,
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Lọc theo rating tối thiểu") @RequestParam(required = false) Integer minRating) {

        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        if (!recipe.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Rating> ratingPage;
        
        if (minRating != null) {
            ratingPage = ratingRepository.findByRecipeAndRatingGreaterThanEqual(recipe.get(), minRating, pageable);
        } else {
            ratingPage = ratingRepository.findByRecipeOrderByCreatedAtDesc(recipe.get(), pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ratings", ratingPage.getContent());
        response.put("currentPage", ratingPage.getNumber());
        response.put("totalElements", ratingPage.getTotalElements());
        response.put("totalPages", ratingPage.getTotalPages());
        response.put("pageSize", ratingPage.getSize());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Thêm đánh giá", description = "Thêm đánh giá và bình luận cho công thức")
    public ResponseEntity<Map<String, Object>> addRating(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long recipeId = Long.valueOf(request.get("recipeId").toString());
        Integer rating = Integer.valueOf(request.get("rating").toString());
        String comment = (String) request.get("comment");
        
        Optional<User> user = userRepository.findById(userId);
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        
        Map<String, Object> response = new HashMap<>();
        
        if (!user.isPresent() || !recipe.isPresent()) {
            response.put("error", "User or Recipe not found");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (rating < 1 || rating > 5) {
            response.put("error", "Rating must be between 1 and 5");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Check if user already rated this recipe
        Optional<Rating> existingRating = ratingRepository.findByUserAndRecipe(user.get(), recipe.get());
        Rating savedRating;
        
        if (existingRating.isPresent()) {
            // Update existing rating
            Rating existing = existingRating.get();
            existing.setRating(rating);
            existing.setComment(comment);
            savedRating = ratingRepository.save(existing);
            response.put("message", "Rating updated successfully");
        } else {
            // Create new rating
            Rating newRating = new Rating(user.get(), recipe.get(), rating, comment);
            savedRating = ratingRepository.save(newRating);
            response.put("message", "Rating added successfully");
        }
        
        response.put("rating", savedRating);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật đánh giá", description = "Cập nhật đánh giá và bình luận")
    public ResponseEntity<Rating> updateRating(@Parameter(description = "ID đánh giá") @PathVariable Long id, @RequestBody Map<String, Object> request) {
        Optional<Rating> optionalRating = ratingRepository.findById(id);
        
        if (optionalRating.isPresent()) {
            Rating rating = optionalRating.get();
            
            if (request.containsKey("rating")) {
                Integer ratingValue = Integer.valueOf(request.get("rating").toString());
                if (ratingValue >= 1 && ratingValue <= 5) {
                    rating.setRating(ratingValue);
                }
            }
            
            if (request.containsKey("comment")) {
                rating.setComment((String) request.get("comment"));
            }
            
            Rating updatedRating = ratingRepository.save(rating);
            return ResponseEntity.ok(updatedRating);
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa đánh giá", description = "Xóa đánh giá và bình luận")
    public ResponseEntity<Map<String, String>> deleteRating(@Parameter(description = "ID đánh giá") @PathVariable Long id) {
        if (ratingRepository.existsById(id)) {
            ratingRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Rating deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/recipe/{recipeId}/stats")
    @Operation(summary = "Thống kê đánh giá", description = "Lấy thống kê đánh giá của công thức")
    public ResponseEntity<Map<String, Object>> getRatingStats(@Parameter(description = "ID công thức") @PathVariable Long recipeId) {
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        
        if (!recipe.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Double averageRating = ratingRepository.getAverageRatingByRecipe(recipe.get());
        Long totalRatings = ratingRepository.countByRecipe(recipe.get());
        
        Map<String, Object> response = new HashMap<>();
        response.put("recipeId", recipeId);
        response.put("averageRating", averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0);
        response.put("totalRatings", totalRatings);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/recipe/{recipeId}")
    @Operation(summary = "Lấy đánh giá của người dùng", description = "Lấy đánh giá của một người dùng cho một công thức cụ thể")
    public ResponseEntity<Rating> getUserRatingForRecipe(
            @Parameter(description = "ID người dùng") @PathVariable Long userId,
            @Parameter(description = "ID công thức") @PathVariable Long recipeId) {
        
        Optional<User> user = userRepository.findById(userId);
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        
        if (!user.isPresent() || !recipe.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Rating> rating = ratingRepository.findByUserAndRecipe(user.get(), recipe.get());
        return rating.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}