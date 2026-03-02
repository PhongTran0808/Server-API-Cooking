package com.cookingapp.server.service;

import com.cookingapp.server.entity.*;
import com.cookingapp.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * Service to initialize sample data in the database
 */
@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (categoryRepository.count() == 0) {
            initializeCategories();
        }
        if (recipeRepository.count() == 0) {
            initializeRecipes();
        }
        if (userRepository.count() == 0) {
            initializeUsers();
        }
        if (ingredientRepository.count() == 0) {
            initializeIngredients();
        }
        if (ratingRepository.count() == 0) {
            initializeRatingsAndFavorites();
        }
    }

    private void initializeCategories() {
        Category category1 = new Category("Món chính", "Các món ăn chính trong bữa cơm", 
            "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/anh_mon_an.png");
        
        Category category2 = new Category("Món tráng miệng", "Các món tráng miệng ngọt ngào", 
            "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/cua.png");
        
        Category category3 = new Category("Đồ uống", "Các loại đồ uống và nước giải khát", 
            "https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/anh_mon_an.png");

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
    }

    private void initializeRecipes() {
        Category mainDish = categoryRepository.findById(1L).orElse(null);
        
        Recipe recipe1 = new Recipe();
        recipe1.setTitle("Phở Bò Truyền Thống");
        recipe1.setDescription("Món phở bò truyền thống Việt Nam với nước dùng đậm đà");
        recipe1.setIngredients("Xương bò, thịt bò, bánh phở, hành tây, gừng, quế, hồi, đinh hương");
        recipe1.setInstructions("1. Ninh xương bò 3-4 tiếng\n2. Ướp thịt bò với gia vị\n3. Chuẩn bị bánh phở và rau thơm");
        recipe1.setImage("https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/anh_mon_an.png");
        recipe1.setServings(4);
        recipe1.setCookingTime(240);
        recipe1.setDifficulty(Recipe.DifficultyLevel.MEDIUM);
        recipe1.setAverageRating(4.8);
        recipe1.setRatingCount(24);
        recipe1.setCategory(mainDish);

        Recipe recipe2 = new Recipe();
        recipe2.setTitle("Cua Rang Me");
        recipe2.setDescription("Cua rang me chua ngọt đậm đà hương vị miền Nam");
        recipe2.setIngredients("Cua biển, me chua, đường, nước mắm, tỏi, ớt");
        recipe2.setInstructions("1. Sơ chế cua sạch\n2. Rang cua với me chua\n3. Nêm nếm gia vị vừa ăn");
        recipe2.setImage("https://raw.githubusercontent.com/PhongTran0808/ServerPic/main/cua.png");
        recipe2.setServings(2);
        recipe2.setCookingTime(45);
        recipe2.setDifficulty(Recipe.DifficultyLevel.EASY);
        recipe2.setAverageRating(4.5);
        recipe2.setRatingCount(18);
        recipe2.setCategory(mainDish);

        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
    }
    
    private void initializeUsers() {
        User admin = new User("admin", "admin@cookingapp.com", "admin123", "Quản trị viên");
        admin.setRole(User.UserRole.ADMIN);
        admin.setBio("Quản trị viên hệ thống ứng dụng nấu ăn");
        
        User chef = new User("chef_minh", "minh@cookingapp.com", "password123", "Chef Minh");
        chef.setBio("Đầu bếp chuyên nghiệp với 10 năm kinh nghiệm");
        
        User foodlover = new User("foodlover", "foodlover@cookingapp.com", "password123", "Người yêu ẩm thực");
        foodlover.setBio("Đam mê khám phá các món ăn mới");
        
        userRepository.save(admin);
        userRepository.save(chef);
        userRepository.save(foodlover);
    }
    
    private void initializeIngredients() {
        // Meat ingredients
        ingredientRepository.save(new Ingredient("Thịt bò", "kg", Ingredient.IngredientCategory.MEAT));
        ingredientRepository.save(new Ingredient("Thịt heo", "kg", Ingredient.IngredientCategory.MEAT));
        ingredientRepository.save(new Ingredient("Thịt gà", "kg", Ingredient.IngredientCategory.MEAT));
        
        // Seafood ingredients
        ingredientRepository.save(new Ingredient("Cua", "kg", Ingredient.IngredientCategory.SEAFOOD));
        ingredientRepository.save(new Ingredient("Tôm", "kg", Ingredient.IngredientCategory.SEAFOOD));
        ingredientRepository.save(new Ingredient("Cá", "kg", Ingredient.IngredientCategory.SEAFOOD));
        
        // Vegetables
        ingredientRepository.save(new Ingredient("Rau muống", "bó", Ingredient.IngredientCategory.VEGETABLE));
        ingredientRepository.save(new Ingredient("Cà chua", "kg", Ingredient.IngredientCategory.VEGETABLE));
        ingredientRepository.save(new Ingredient("Hành lá", "bó", Ingredient.IngredientCategory.VEGETABLE));
        
        // Spices
        ingredientRepository.save(new Ingredient("Muối", "gói", Ingredient.IngredientCategory.SPICE));
        ingredientRepository.save(new Ingredient("Đường", "gói", Ingredient.IngredientCategory.SPICE));
        ingredientRepository.save(new Ingredient("Tiêu", "gói", Ingredient.IngredientCategory.SPICE));
        
        // Grains
        ingredientRepository.save(new Ingredient("Gạo", "kg", Ingredient.IngredientCategory.GRAIN));
        ingredientRepository.save(new Ingredient("Bún", "gói", Ingredient.IngredientCategory.GRAIN));
        ingredientRepository.save(new Ingredient("Phở", "gói", Ingredient.IngredientCategory.GRAIN));
    }
    
    private void initializeRatingsAndFavorites() {
        User chef = userRepository.findByUsername("chef_minh").orElse(null);
        User foodlover = userRepository.findByUsername("foodlover").orElse(null);
        Recipe pho = recipeRepository.findById(1L).orElse(null);
        Recipe cua = recipeRepository.findById(2L).orElse(null);

        if (chef != null && pho != null) {
            Rating rating1 = new Rating(chef, pho, 5, "Công thức tuyệt vời! Nước dùng rất đậm đà.");
            ratingRepository.save(rating1);
            
            Favorite favorite1 = new Favorite(chef, pho);
            favoriteRepository.save(favorite1);
        }

        if (foodlover != null && cua != null) {
            Rating rating2 = new Rating(foodlover, cua, 4, "Món ăn ngon, dễ làm. Sẽ thử lại lần sau.");
            ratingRepository.save(rating2);
            
            Favorite favorite2 = new Favorite(foodlover, cua);
            favoriteRepository.save(favorite2);
        }
    }
}