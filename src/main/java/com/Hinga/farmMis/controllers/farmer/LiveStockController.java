package com.Hinga.farmMis.controllers.farmer;

import com.Hinga.farmMis.Constants.LivestockStatus;
import com.Hinga.farmMis.Model.Livestock;
import com.Hinga.farmMis.Model.Users;
import com.Hinga.farmMis.repository.UserRepository;
import com.Hinga.farmMis.services.JwtService;
import com.Hinga.farmMis.services.farmer.LivestockService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/livestock/v2")
public class LiveStockController {
    private final LivestockService livestockService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LiveStockController(LivestockService livestockService,
                             JwtService jwtService,
                             UserRepository userRepository) {
        this.livestockService = livestockService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping(consumes= MediaType.MULTIPART_FORM_DATA_VALUE, path = "/add")
    public ResponseEntity<?> createLivestock(@RequestHeader("Authorization") String token,
                                           @RequestParam(value="type",required = true) String type,
                                           @RequestParam(value="breeds",required = true) String breeds,
                                           @RequestParam(value = "count") int count,
                                           @RequestParam(value = "description") String description,
                                           @RequestParam(value = "weight") double weight,
                                           @RequestParam(value = "price") double price,
                                           @RequestParam(value = "quantity") int quantity,
                                           @RequestParam(value = "image", required=true) MultipartFile imageFile) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid token format");
            }

            String jwtToken = token.substring(7);

            
            // Validate token
            if (!jwtService.isTokenValid(jwtToken)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            // Get user ID from token
            Long userId = jwtService.extractId(jwtToken);
            Livestock livestock = new Livestock();
            
            // Set the farmer (user) for the livestock
            Users farmer = userRepository.findById(userId);
            if(farmer==null) {
                return ResponseEntity.status(401).body("User not found");
            }

            livestock.setFarmer(farmer);
            livestock.setType(type);
            livestock.setCount(count);
            livestock.setDescription(description);
            livestock.setWeight(weight);
            livestock.setPrice(price);
            livestock.setQuantity(quantity);
            livestock.setStatus(LivestockStatus.valueOf("HEALTHY"));
            livestock.setBirthDate(LocalDate.now());
            livestock.setBreed(breeds);

            // Add livestock
            Livestock savedLivestock = livestockService.addLivestock(livestock, imageFile);
            return ResponseEntity.status(201).body("Livestock added successfully: " + savedLivestock);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating livestock: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllLivestocks(@RequestHeader("Authorization") String token) {
      try{
          if(jwtService.isTokenInvalidated(token)){
              return  ResponseEntity.status(404).body("Invalid token");
          }
          // Token validation logic
          if (!isValidToken(token)) {
              return ResponseEntity.status(401).body("Invalid token");
          }
          String jwtToken = token.substring(7);
          Long Id = Long.parseLong(String.valueOf(jwtService.extractId(jwtToken)));
          List<Livestock> livestock=livestockService.getAllLiveStocks(Id);
          return ResponseEntity.status(201).body(livestock);


      }catch (Exception e){
          return ResponseEntity.status(400).body("Error In Getting livestock" + e.getMessage());
      }

    }
    private boolean isValidToken(String token) {
        try {
            String jwtToken = token.substring(7);
            Long Id = (long) Integer.parseInt(String.valueOf(jwtService.extractId(jwtToken)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
