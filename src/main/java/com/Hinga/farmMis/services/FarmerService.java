package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Model.Farmer;
import com.Hinga.farmMis.repository.FarmerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class FarmerService {


    private final FarmerRepository farmerRepository;
    private final PasswordEncoder passwordEncoder;

    public FarmerService(FarmerRepository farmerRepository, PasswordEncoder passwordEncoder) {
        this.farmerRepository = farmerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Farmer registerFarmer(Farmer farmer) {
        // Hash password before saving
        farmer.setPassword(passwordEncoder.encode(farmer.getPassword()));

        // Set a default role if not provided
        if (farmer.getRole() == null || farmer.getRole().isEmpty()) {
            farmer.setRole("ROLE_USER");
        }

        return farmerRepository.save(farmer);
    }
}
