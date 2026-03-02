package com.cookingapp.server.repository;

import com.cookingapp.server.entity.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
    @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Ingredient> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    List<Ingredient> findByCategoryOrderByName(Ingredient.IngredientCategory category);
    
    @Query("SELECT i FROM Ingredient i ORDER BY i.name")
    Page<Ingredient> findAllOrderByName(Pageable pageable);
    
    boolean existsByNameIgnoreCase(String name);
}