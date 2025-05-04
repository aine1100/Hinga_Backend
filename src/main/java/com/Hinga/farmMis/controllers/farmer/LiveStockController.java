package com.Hinga.farmMis.controllers.farmer;

import com.Hinga.farmMis.Constants.LivestockStatus;
import com.Hinga.farmMis.Model.Livestock;
import com.Hinga.farmMis.Model.Users;
import com.Hinga.farmMis.repository.UserRepository;
import com.Hinga.farmMis.services.JwtService;
import com.Hinga.farmMis.services.farmer.LivestockService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/add")
    public ResponseEntity<?> createLivestock(@RequestHeader("Authorization") String token,
                                             @RequestParam(value = "type", required = true) String type,
                                             @RequestParam(value = "breeds", required = true) String breeds,
                                             @RequestParam(value = "count") int count,
                                             @RequestParam(value = "description") String description,
                                             @RequestParam(value = "weight") double weight,
                                             @RequestParam(value = "price") double price,
                                             @RequestParam(value = "quantity") int quantity,
                                             @RequestParam(value = "image", required = true) MultipartFile imageFile) {
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
            if (farmer == null) {
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

    @GetMapping("/get")
    public ResponseEntity<?> getAllLivestocks(@RequestHeader("Authorization") String token) {
        try {
            if (jwtService.isTokenInvalidated(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }
            // Token validation logic
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }
            String jwtToken = token.substring(7);
            Long Id = Long.parseLong(String.valueOf(jwtService.extractId(jwtToken)));
            List<Livestock> livestock = livestockService.getAllLiveStocks(Id);
            return ResponseEntity.status(200).body(livestock);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error In Getting livestock: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivestock(@RequestHeader("Authorization") String token,
                                                @PathVariable Long id) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }

            String jwtToken = token.substring(7);

            // Validate token
            if (!jwtService.isTokenValid(jwtToken)) {
                return ResponseEntity.status(401).build();
            }

            // Get user ID from token
            Long userId = jwtService.extractId(jwtToken);

            // Delete livestock
            livestockService.deleteLivestock(id, userId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateLivestock(@RequestHeader("Authorization") String token,
                                             @PathVariable Long id,
                                             @RequestParam(value = "type", required = false) String type,
                                             @RequestParam(value = "breeds", required = false) String breeds,
                                             @RequestParam(value = "count", required = false) Integer count,
                                             @RequestParam(value = "description", required = false) String description,
                                             @RequestParam(value = "weight", required = false) Double weight,
                                             @RequestParam(value = "price", required = false) Double price,
                                             @RequestParam(value = "quantity", required = false) Integer quantity,
                                             @RequestParam(value = "status", required = false) String status,
                                             @RequestParam(value = "image", required = false) MultipartFile imageFile) {
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

            // Create updated livestock object
            Livestock livestock = new Livestock();
            livestock.setType(type);
            livestock.setBreed(breeds);
            livestock.setCount(count != null ? count : 0);
            livestock.setDescription(description);
            livestock.setWeight(weight != null ? weight : 0.0);
            livestock.setPrice(price != null ? price : 0.0);
            livestock.setQuantity(quantity != null ? quantity : 0);
            if (status != null) {
                livestock.setStatus(LivestockStatus.valueOf(status));
            }

            // Update livestock
            Livestock updatedLivestock = livestockService.updateLiveStock(userId, id, livestock, imageFile);
            return ResponseEntity.ok("Livestock updated successfully: " + updatedLivestock);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating livestock: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllLivestockForBuyers(@RequestHeader("Authorization") String token) {
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
            Users user = userRepository.findById(userId);
            if (user == null) {
                return ResponseEntity.status(401).body("User not found");
            }

            // Check if user is a BUYER
            if (!"BUYER".equalsIgnoreCase(String.valueOf(user.getUserRole()))) {
                return ResponseEntity.status(403).body("Access denied: Only buyers can view all livestock");
            }

            // Fetch all livestock
            List<Livestock> livestock = livestockService.getAllLivestockForBuyers();
            return ResponseEntity.ok(livestock);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving livestock: " + e.getMessage());
        }
    }

    private boolean isValidToken(String token) {
        try {
            String jwtToken = token.substring(7);
            Long Id = Long.parseLong(String.valueOf(jwtService.extractId(jwtToken)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}