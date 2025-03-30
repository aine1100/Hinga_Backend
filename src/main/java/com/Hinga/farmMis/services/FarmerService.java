package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Model.Farmer;
import com.Hinga.farmMis.repository.FarmerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Farmer farmerLogin(Farmer farmer){
       Optional<Farmer> farmer1=farmerRepository.findByEmail(farmer.getEmail());
        if(!farmer1.isPresent()){
            System.out.println("Farmer with this email is not found");
            return null;
        }
        Farmer existingFarmer=farmer1.get();
        if(!passwordEncoder.matches(farmer.getPassword(),existingFarmer.getPassword())){
            System.out.println("Farmer with this password is not found");
        }
       return existingFarmer;
    }
}
