package com.cookingapp.server.repository;

import com.cookingapp.server.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    @Query("SELECT r FROM Recipe r WHERE r.averageRating >= 4.5 ORDER BY r.averageRating DESC")
    List<Recipe> findFeaturedRecipes();
    
    List<Recipe> findByCategoryId(Long categoryId);
    
    @Query("SELECT r FROM Recipe r WHERE r.title LIKE %?1% OR r.description LIKE %?1%")
    List<Recipe> findByTitleOrDescriptionContaining(String keyword);
    
    // New paginated methods
    Page<Recipe> findByCategoryIdOrderByCreatedAtDesc(Long categoryId, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.averageRating >= 4.0 ORDER BY r.averageRating DESC, r.ratingCount DESC")
    Page<Recipe> findFeaturedRecipesPaginated(Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY r.createdAt DESC")
    Page<Recipe> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.difficulty = :difficulty ORDER BY r.createdAt DESC")
    Page<Recipe> findByDifficulty(@Param("difficulty") Recipe.DifficultyLevel difficulty, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.cookingTime <= :maxTime ORDER BY r.cookingTime ASC")
    Page<Recipe> findByMaxCookingTime(@Param("maxTime") Integer maxTime, Pageable pageable);
}