package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Constants.UserRoles;
import com.Hinga.farmMis.Dto.Request.RegisterDto;
import com.Hinga.farmMis.Dto.Request.ResetPassword;
import com.Hinga.farmMis.Model.Users;
import com.Hinga.farmMis.services.AuthService;
import com.Hinga.farmMis.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth/v2")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/Register")
    public ResponseEntity<?> RegisterUser(@RequestBody RegisterDto user) {
        try {
            if (user.getPassword().length() <= 5) {
                return ResponseEntity.status(400).body("Please enter a password of at least 5 characters");
            }

            Users users = new Users();
            users.setFirstName(user.getFirstName());
            users.setLastName(user.getLastName());
            users.setEmail(user.getEmail());
            users.setPassword(user.getPassword());
            users.setPhoneNumber(user.getPhone());
            users.setAddress(user.getAddress());
            users.setUserRole(user.getRole());

            Users savedUser = authService.registerUser(users);
            String token = jwtService.generateToken(savedUser);

            return ResponseEntity.ok("User registered successfully. Token: " + token);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("An error occurred while registering the user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> LoginUser(@RequestBody RegisterDto user) {
        try {
            Users user1 = new Users();
            user1.setPassword(user.getPassword());
            user1.setEmail(user.getEmail());

            Users savedUser = authService.userLogin(user1);

            if (savedUser == null) {
                return ResponseEntity.status(400).body("Invalid email or password");
            }
            UserRoles roles = savedUser.getUserRole();

            String token = jwtService.generateToken(savedUser);

            Map<String, Object> response = new HashMap<>();
            response.put("message", savedUser.getFirstName() + " logged in successfully.");
            response.put("token", token);
            response.put("role", roles);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("An error occurred while logging in the user: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestBody String email){
        try {
            Users user = authService.getUserByEmail(email);
            return ResponseEntity.status(200).body(user);
        } catch (Exception e){
            return ResponseEntity.status(400).body("An error occurred while getting the user");
        }
    }

    // ✅ Step 1: Send Reset Password Email
    @PostMapping("/reset-password/request")

    public ResponseEntity<?> requestResetPassword(@RequestBody ResetPassword email) {
        try {
            authService.sendPasswordResetEmail(email.getEmail());
            return ResponseEntity.ok("Password reset email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("An error occurred while sending reset email: " + e.getMessage());
        }
    }

    // ✅ Step 2: Reset Password using Token
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> confirmResetPassword(@RequestParam("token") String token,
                                                  @RequestParam("newPassword") String newPassword) {
        try {
            if (newPassword.length() <= 5) {
                return ResponseEntity.badRequest().body("Password must be at least 6 characters long");
            }

            authService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password has been reset successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("An error occurred while resetting the password: " + e.getMessage());
        }
    }
}
