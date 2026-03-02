package com.cookingapp.server.repository;

import com.cookingapp.server.entity.Favorite;
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
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Optional<Favorite> findByUserAndRecipe(User user, Recipe recipe);
    
    boolean existsByUserAndRecipe(User user, Recipe recipe);
    
    void deleteByUserAndRecipe(User user, Recipe recipe);
    
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.recipe = :recipe")
    Long countByRecipe(@Param("recipe") Recipe recipe);
}