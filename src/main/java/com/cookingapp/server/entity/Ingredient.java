package com.cookingapp.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    private String unit; // kg, gram, lít, thìa, etc.
    private String image;
    
    @Enumerated(EnumType.STRING)
    private IngredientCategory category = IngredientCategory.OTHER;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructors
    public Ingredient() {}
    
    public Ingredient(String name, String unit, IngredientCategory category) {
        this.name = name;
        this.unit = unit;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public IngredientCategory getCategory() { return category; }
    public void setCategory(IngredientCategory category) { this.category = category; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public static enum IngredientCategory {
        MEAT, SEAFOOD, VEGETABLE, FRUIT, DAIRY, GRAIN, SPICE, SAUCE, OTHER
    }
}