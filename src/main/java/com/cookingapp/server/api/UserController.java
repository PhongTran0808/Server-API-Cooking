package com.cookingapp.server.api;

import com.cookingapp.server.entity.User;
import com.cookingapp.server.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API quản lý người dùng")
@Validated
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private static final long REQUEST_TIMEOUT_MS = 10000; // 10 seconds timeout
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");

    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // DTO Classes for request/response
    public static class LoginRequest {
        @NotBlank(message = "Username or email is required")
        private String usernameOrEmail;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
        
        // Getters and setters
        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        private String password;
        
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        private String fullName;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản", description = "Đăng ký tài khoản người dùng mới với mã hóa mật khẩu")
    public DeferredResult<ResponseEntity<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
        DeferredResult<ResponseEntity<Map<String, Object>>> deferredResult = new DeferredResult<>(REQUEST_TIMEOUT_MS);
        
        // Set timeout handler
        deferredResult.onTimeout(() -> {
            logger.warning("Registration request timed out for user: " + request.getUsername());
            Map<String, Object> timeoutResponse = new HashMap<>();
            timeoutResponse.put("error", "Request timed out. Please try again.");
            deferredResult.setResult(ResponseEntity.status(408).body(timeoutResponse));
        });
        
        // Process registration asynchronously to prevent hanging
        CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Processing registration for user: " + request.getUsername());
                
                // Validate input
                Map<String, Object> validationResult = validateRegistrationInput(request);
                if (validationResult.containsKey("error")) {
                    return ResponseEntity.badRequest().body(validationResult);
                }
                
                // Check if username already exists
                if (userRepository.existsByUsername(request.getUsername())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", "Tên đăng nhập đã tồn tại");
                    response.put("field", "username");
                    return ResponseEntity.badRequest().body(response);
                }
                
                // Check if email already exists
                if (userRepository.existsByEmail(request.getEmail())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", "Email đã được đăng ký");
                    response.put("field", "email");
                    return ResponseEntity.badRequest().body(response);
                }
                
                // Create new user with encrypted password
                User user = new User();
                user.setUsername(request.getUsername().trim());
                user.setEmail(request.getEmail().trim().toLowerCase());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setFullName(request.getFullName() != null ? request.getFullName().trim() : null);
                user.setRole(User.UserRole.USER);
                
                User savedUser = userRepository.save(user);
                
                // Remove password from response
                Map<String, Object> response = new HashMap<>();
                Map<String, Object> userResponse = createUserResponse(savedUser);
                response.put("user", userResponse);
                response.put("message", "Đăng ký thành công!");
                response.put("token", "mock-jwt-token-" + savedUser.getId()); // Mock token for now
                
                logger.info("User registered successfully: " + request.getUsername());
                return ResponseEntity.ok(response);
                
            } catch (Exception e) {
                logger.severe("Registration failed for user: " + request.getUsername() + " - " + e.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Đăng ký thất bại. Vui lòng thử lại.");
                return ResponseEntity.status(500).body(errorResponse);
            }
        }).orTimeout(REQUEST_TIMEOUT_MS - 1000, TimeUnit.MILLISECONDS)
        .whenComplete((result, throwable) -> {
            if (throwable != null) {
                logger.severe("Registration error: " + throwable.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Lỗi hệ thống. Vui lòng thử lại.");
                deferredResult.setResult(ResponseEntity.status(500).body(errorResponse));
            } else {
                deferredResult.setResult(result);
            }
        });
        
        return deferredResult;
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Đăng nhập bằng username/email và mật khẩu với mã hóa an toàn")
    public DeferredResult<ResponseEntity<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        DeferredResult<ResponseEntity<Map<String, Object>>> deferredResult = new DeferredResult<>(REQUEST_TIMEOUT_MS);
        
        // Set timeout handler
        deferredResult.onTimeout(() -> {
            logger.warning("Login request timed out for user: " + request.getUsernameOrEmail());
            Map<String, Object> timeoutResponse = new HashMap<>();
            timeoutResponse.put("error", "Request timed out. Please try again.");
            deferredResult.setResult(ResponseEntity.status(408).body(timeoutResponse));
        });
        
        // Process login asynchronously to prevent hanging
        CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Processing login for user: " + request.getUsernameOrEmail());
                
                String usernameOrEmail = request.getUsernameOrEmail().trim();
                String password = request.getPassword();
                
                // Find user by username or email
                Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
                
                Map<String, Object> response = new HashMap<>();
                
                if (userOpt.isEmpty()) {
                    response.put("error", "Tên đăng nhập/email hoặc mật khẩu không đúng");
                    return ResponseEntity.badRequest().body(response);
                }
                
                User user = userOpt.get();
                
                // Verify password using BCrypt
                if (!passwordEncoder.matches(password, user.getPassword())) {
                    response.put("error", "Tên đăng nhập/email hoặc mật khẩu không đúng");
                    return ResponseEntity.badRequest().body(response);
                }
                
                // Successful login
                Map<String, Object> userResponse = createUserResponse(user);
                response.put("user", userResponse);
                response.put("message", "Đăng nhập thành công!");
                response.put("token", "mock-jwt-token-" + user.getId()); // Mock token for now
                
                logger.info("User logged in successfully: " + usernameOrEmail);
                return ResponseEntity.ok(response);
                
            } catch (Exception e) {
                logger.severe("Login failed for user: " + request.getUsernameOrEmail() + " - " + e.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Đăng nhập thất bại. Vui lòng thử lại.");
                return ResponseEntity.status(500).body(errorResponse);
            }
        }).orTimeout(REQUEST_TIMEOUT_MS - 1000, TimeUnit.MILLISECONDS)
        .whenComplete((result, throwable) -> {
            if (throwable != null) {
                logger.severe("Login error: " + throwable.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Lỗi hệ thống. Vui lòng thử lại.");
                deferredResult.setResult(ResponseEntity.status(500).body(errorResponse));
            } else {
                deferredResult.setResult(result);
            }
        });
        
        return deferredResult;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách người dùng", description = "Lấy danh sách tất cả người dùng với phân trang")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.findAll(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("users", userPage.getContent().stream()
                .map(this::createUserResponse)
                .toList());
            response.put("currentPage", userPage.getNumber());
            response.put("totalElements", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            response.put("pageSize", userPage.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error getting users: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unable to retrieve users");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin người dùng", description = "Lấy thông tin chi tiết người dùng theo ID")
    public ResponseEntity<Map<String, Object>> getUserById(@Parameter(description = "ID người dùng") @PathVariable Long id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                Map<String, Object> response = createUserResponse(user.get());
                return ResponseEntity.ok(Map.of("user", response));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.severe("Error getting user by ID: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Unable to retrieve user"));
        }
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Lấy thông tin người dùng theo username", description = "Lấy thông tin người dùng theo tên đăng nhập")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@Parameter(description = "Tên đăng nhập") @PathVariable String username) {
        try {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                Map<String, Object> response = createUserResponse(user.get());
                return ResponseEntity.ok(Map.of("user", response));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.severe("Error getting user by username: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Unable to retrieve user"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin người dùng", description = "Cập nhật thông tin người dùng")
    public ResponseEntity<Map<String, Object>> updateUser(@Parameter(description = "ID người dùng") @PathVariable Long id, @RequestBody Map<String, String> userDetails) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                
                if (userDetails.containsKey("fullName")) {
                    user.setFullName(userDetails.get("fullName"));
                }
                if (userDetails.containsKey("bio")) {
                    user.setBio(userDetails.get("bio"));
                }
                if (userDetails.containsKey("avatar")) {
                    user.setAvatar(userDetails.get("avatar"));
                }
                
                User updatedUser = userRepository.save(user);
                Map<String, Object> response = createUserResponse(updatedUser);
                return ResponseEntity.ok(Map.of("user", response, "message", "Profile updated successfully"));
            }
            
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.severe("Error updating user: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Unable to update user"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa người dùng", description = "Xóa tài khoản người dùng")
    public ResponseEntity<Map<String, String>> deleteUser(@Parameter(description = "ID người dùng") @PathVariable Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.severe("Error deleting user: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Unable to delete user"));
        }
    }

    // Helper methods
    private Map<String, Object> validateRegistrationInput(RegisterRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        // Validate username format
        if (!USERNAME_PATTERN.matcher(request.getUsername()).matches()) {
            result.put("error", "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới");
            result.put("field", "username");
            return result;
        }
        
        // Validate email format
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            result.put("error", "Định dạng email không hợp lệ");
            result.put("field", "email");
            return result;
        }
        
        return result; // No errors
    }
    
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("avatar", user.getAvatar());
        response.put("bio", user.getBio());
        response.put("role", user.getRole());
        response.put("createdAt", user.getCreatedAt());
        response.put("updatedAt", user.getUpdatedAt());
        // Note: Password is intentionally excluded from response
        return response;
    }
}