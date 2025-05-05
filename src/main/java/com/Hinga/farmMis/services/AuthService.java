package com.Hinga.farmMis.services;


import com.Hinga.farmMis.Model.Farmer;
import com.Hinga.farmMis.Model.Users;

import com.Hinga.farmMis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class AuthService {
    // Make regex constant and private
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JavaMailSender mailSender;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    // Changed return type to boolean for isValidEmail

        public boolean isValidEmail(String email) {
            if (email == null || email.trim().isEmpty()) {
                return false;
            }
            return email.trim().matches(EMAIL_REGEX);
        }


    public Users registerUser(Users user) {
        // Validate email
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalStateException("Email already registered");
        }

        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set a default role if not provided
        if (user.getUserRole() == null ) {
          throw new IllegalArgumentException("User must have a role");
        }

        return userRepository.save(user);
    }

    public Users userLogin(Users user) {
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        Optional<Users> userOptional = Optional.ofNullable(userRepository.findByEmail(user.getEmail()));

        if (!userOptional.isPresent()) {
            throw new IllegalStateException("Farmer with this email not found");
        }

        Users existingUser = userOptional.get();

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            throw new IllegalStateException("Invalid password");
        }



        return existingUser;
    }


    public Users getUserByEmail(String email) {
        Optional<Users> usersOptional = Optional.ofNullable(userRepository.findByEmail(email));
        if (usersOptional.isEmpty()) {
            throw new IllegalStateException("Farmer with this email not found");
        }
        return usersOptional.get();
    }

    public void sendPasswordResetEmail(String email) {
        String trimmedEmail = email != null ? email.trim() : null;
        if (!isValidEmail(trimmedEmail)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        Optional<Users> user = Optional.ofNullable(userRepository.findByEmail(trimmedEmail));
        if (user.isEmpty()) {
            throw new IllegalStateException("Farmer with this email not found");
        }

        String token = UUID.randomUUID().toString();
        Users users = user.get();
        users.setResetPasswordToken(token);
        users.setResetPasswordTokenCreatedAt(LocalDateTime.now()); // Set the timestamp
        userRepository.save(users);

        String resetUrl = "http://localhost:5173/reset-password/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(trimmedEmail);
        message.setSubject("Reset your password");
        message.setText("Click the link to reset your password: " + resetUrl);

        mailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        Users user = userRepository.findByResetPasswordToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }
        // Check if token is expired (e.g., 1 hour)
        LocalDateTime createdAt = user.getResetPasswordTokenCreatedAt();
        if (createdAt == null || Duration.between(createdAt, LocalDateTime.now()).toHours() >= 1) {
            throw new IllegalArgumentException("Reset token has expired");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null); // Invalidate token
        user.setResetPasswordTokenCreatedAt(null); // Clear timestamp
        userRepository.save(user);
    }
}