//package com.Hinga.farmMis.services;
//
//import com.Hinga.farmMis.Model.Farmer;
//import com.Hinga.farmMis.repository.FarmerRepository;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class FarmerService {
//    // Make regex constant and private
//    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
//
//    private final FarmerRepository farmerRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public FarmerService(FarmerRepository farmerRepository, PasswordEncoder passwordEncoder) {
//        this.farmerRepository = farmerRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    // Changed return type to boolean for isValidEmail
//    public boolean isValidEmail(String email) {
//        if (email == null) {
//            return false;
//        }
//        return email.matches(EMAIL_REGEX);
//    }
//
//    public Farmer registerFarmer(Farmer farmer) {
//        // Validate email
//        if (!isValidEmail(farmer.getEmail())) {
//            throw new IllegalArgumentException("Invalid email format");
//        }
//
//        // Check if email already exists
//        if (farmerRepository.findByEmail(farmer.getEmail()).isPresent()) {
//            throw new IllegalStateException("Email already registered");
//        }
//
//        // Hash password before saving
//        farmer.setPassword(passwordEncoder.encode(farmer.getPassword()));
//
//        // Set a default role if not provided
//        if (farmer.getRole() == null || farmer.getRole().isEmpty()) {
//            farmer.setRole("ROLE_USER");
//        }
//
//        return farmerRepository.save(farmer);
//    }
//
//    public Farmer farmerLogin(Farmer farmer) {
//        if (!isValidEmail(farmer.getEmail())) {
//            throw new IllegalArgumentException("Invalid email format");
//        }
//
//        Optional<Farmer> farmerOptional = farmerRepository.findByEmail(farmer.getEmail());
//
//        if (!farmerOptional.isPresent()) {
//            throw new IllegalStateException("Farmer with this email not found");
//        }
//
//        Farmer existingFarmer = farmerOptional.get();
//
//        if (!passwordEncoder.matches(farmer.getPassword(), existingFarmer.getPassword())) {
//            throw new IllegalStateException("Invalid password");
//        }
//
//        return existingFarmer;
//    }
//    public Farmer ResetPassword(String email,String password){
//        Optional<Farmer> farmerOptional=farmerRepository.findByEmail(email);
//        if(!farmerOptional.isPresent()){
//            throw new IllegalStateException("Farmer with this email not found");
//        }
//        Farmer farmer=farmerOptional.get();
//        farmer.setPassword(passwordEncoder.encode(password));
//        farmerRepository.save(farmer);
//        return farmer;
//    }
//
//    public Farmer getFarmerByEmail(String email) {
//        Optional<Farmer> farmerOptional = farmerRepository.findByEmail(email);
//        if (!farmerOptional.isPresent()) {
//            throw new IllegalStateException("Farmer with this email not found");
//        }
//        return farmerOptional.get();
//    }
//}