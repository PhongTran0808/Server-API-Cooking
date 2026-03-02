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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/search")
@Tag(name = "Search", description = "API tìm kiếm công thức nấu ăn")
public class SearchController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping("/recipes")
    @Operation(summary = "Tìm kiếm công thức", description = "Tìm kiếm công thức theo từ khóa, độ khó, thời gian nấu")
    public ResponseEntity<Map<String, Object>> searchRecipes(
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(required = false) String keyword,
            @Parameter(description = "Độ khó (EASY, MEDIUM, HARD)") @RequestParam(required = false) String difficulty,
            @Parameter(description = "Thời gian nấu tối đa (phút)") @RequestParam(required = false) Integer maxTime,
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            recipePage = recipeRepository.searchByKeyword(keyword.trim(), pageable);
        } else if (difficulty != null) {
            try {
                Recipe.DifficultyLevel difficultyLevel = Recipe.DifficultyLevel.valueOf(difficulty.toUpperCase());
                recipePage = recipeRepository.findByDifficulty(difficultyLevel, pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid difficulty level"));
            }
        } else if (maxTime != null) {
            recipePage = recipeRepository.findByMaxCookingTime(maxTime, pageable);
        } else {
            recipePage = recipeRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("recipes", recipePage.getContent());
        response.put("currentPage", recipePage.getNumber());
        response.put("totalElements", recipePage.getTotalElements());
        response.put("totalPages", recipePage.getTotalPages());
        response.put("pageSize", recipePage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Gợi ý tìm kiếm", description = "Lấy danh sách gợi ý từ khóa tìm kiếm")
    public ResponseEntity<Map<String, Object>> getSearchSuggestions() {
        Map<String, Object> suggestions = new HashMap<>();
        suggestions.put("popularKeywords", new String[]{"phở", "bún chả", "cơm tấm", "bánh mì", "chả cá"});
        suggestions.put("difficulties", new String[]{"EASY", "MEDIUM", "HARD"});
        suggestions.put("cookingTimes", new Integer[]{15, 30, 60, 120});
        
        return ResponseEntity.ok(suggestions);
    }
}