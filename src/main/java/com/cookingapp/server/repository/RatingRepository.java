package com.cookingapp.server.repository;

import com.cookingapp.server.entity.Rating;
import com.cookingapp.server.entity.Recipe;
import com.cookingapp.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    Page<Rating> findByRecipeOrderByCreatedAtDesc(Recipe recipe, Pageable pageable);
    
    Optional<Rating> findByUserAndRecipe(User user, Recipe recipe);
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.recipe = :recipe")
    Double getAverageRatingByRecipe(@Param("recipe") Recipe recipe);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.recipe = :recipe")
    Long countByRecipe(@Param("recipe") Recipe recipe);
    
    @Query("SELECT r FROM Rating r WHERE r.recipe = :recipe AND r.rating >= :minRating ORDER BY r.createdAt DESC")
    Page<Rating> findByRecipeAndRatingGreaterThanEqual(@Param("recipe") Recipe recipe, @Param("minRating") Integer minRating, Pageable pageable);
}