package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Model.Farmer;
import com.Hinga.farmMis.services.FarmerService;
import com.Hinga.farmMis.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class FarmerController {
    private final FarmerService farmerService;
    private final JwtService jwtService;

    public FarmerController(FarmerService farmerService, JwtService jwtService) {
        this.farmerService = farmerService;
        this.jwtService = jwtService;
    }

    @PostMapping("/farmerRegister")
    public ResponseEntity<String> registerFarmer(@RequestBody Farmer farmer) {
        // Register the farmer and save to the database
        if(farmer.getPassword().length()<=5){
            return ResponseEntity.status(400).body("Please enter the password of atleast 5 characters");
        }
        Farmer savedFarmer = farmerService.registerFarmer(farmer);

        // Generate JWT token for the newly registered farmer
        String token = jwtService.generateToken(savedFarmer.getEmail());
        // Respond with a success message and the generated token
        return ResponseEntity.ok("Farmer registered successfully. Token: " + token);
    }

    @PostMapping("/farmerLogin")
    public ResponseEntity<?> farmerLogin(@RequestBody Farmer farmer){
        Farmer savedFarmer=farmerService.farmerLogin(farmer);

        if(savedFarmer==null){
            return ResponseEntity.status(400).body("Invalid email or password");
        }
        String token=jwtService.generateToken(savedFarmer.getEmail());
        return ResponseEntity.ok(savedFarmer+" logged in successfully. Token: " + token);
    }
    @GetMapping("/farmer")
    public ResponseEntity<?> getFarmer(@RequestHeader("Authorization") String token){
        String jwtToken = token.substring(7);
        String username = jwtService.extractUsername(jwtToken);

        if (username == null) {
            return ResponseEntity.status(401).body(null);
        }
       Farmer farmer=farmerService.getFarmerByEmail(username);
        if(farmer==null){
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(farmer);

    }
}
