//package com.Hinga.farmMis.controllers;
//
//import com.Hinga.farmMis.Model.Farm;
//import com.Hinga.farmMis.services.FarmService;
//import com.Hinga.farmMis.services.JwtService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/farms")
//@CrossOrigin(
//        origins = "http://localhost:5173",
//        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
//        allowedHeaders = {"Authorization", "Content-Type"},
//        exposedHeaders = {"Authorization"},
//        allowCredentials = "false"
//)
//public class FarmController {
//
//    private final FarmService farmService;
//    private final JwtService jwtService;
//
//    public FarmController(FarmService farmService, JwtService jwtService) {
//        this.farmService = farmService;
//        this.jwtService = jwtService;
//    }
//
//    // ✅ Create a farm (Only allowed if the user is authenticated)
//    @PostMapping("/create")
//    public ResponseEntity<Farm> createFarm(@RequestHeader("Authorization") String token, @RequestBody Farm farm) {
//        String jwtToken = token.substring(7); // Remove "Bearer " prefix
//        if(jwtService.isTokenInvalidated(jwtToken)){
//            return  ResponseEntity.status(404).body(null);
//        }
//        String username = jwtService.extractUsername(jwtToken);
//
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//
//        Farm savedFarm = farmService.createFarm(farm, username);
//        return ResponseEntity.ok(savedFarm);
//    }
//
//    // ✅ Get only farms belonging to the logged-in farmer
//    @GetMapping("/my-farms")
//    public ResponseEntity<List<Farm>> getFarmsByFarmer(@RequestHeader("Authorization") String token) {
//        String jwtToken = token.substring(7);
//        if(jwtService.isTokenInvalidated(jwtToken)){
//            return  ResponseEntity.status(404).body(null);
//        }
//        String username = jwtService.extractUsername(jwtToken);
//
//
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//        List<Farm> farms = farmService.getFarmsByOwner(username);
//        return ResponseEntity.ok(farms);
//    }
//
//    // ✅ Get a specific farm by ID (Only if the farmer owns it)
//    @GetMapping("/{id}")
//    public ResponseEntity<Optional<Farm>> getFarmById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
//        String jwtToken = token.substring(7);
//        if(jwtService.isTokenInvalidated(jwtToken)){
//            return  ResponseEntity.status(404).body(null);
//        }
//        String username = jwtService.extractUsername(jwtToken);
//
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//        Optional<Farm> farm = farmService.getFarmById(id, username);
//        return farm.isPresent() ? ResponseEntity.ok(farm) : ResponseEntity.status(403).build(); // Forbidden if not the owner
//    }
//
//    // ✅ Update farm (Only if the farmer owns it)
//    @PutMapping("/update/{id}")
//    public ResponseEntity<Farm> updateFarm(@PathVariable Long id, @RequestBody Farm farm, @RequestHeader("Authorization") String token) {
//        String jwtToken = token.substring(7);
//        if(jwtService.isTokenInvalidated(jwtToken)){
//            return  ResponseEntity.status(404).body(null);
//        }
//        String username = jwtService.extractUsername(jwtToken);
//
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//        Farm updatedFarm = farmService.updateFarm(id, farm, username);
//        return (updatedFarm != null) ? ResponseEntity.ok(updatedFarm) : ResponseEntity.status(403).build(); // Forbidden if not the owner
//    }
//
//    // ✅ Delete farm (Only if the farmer owns it)
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteFarm(@PathVariable Long id, @RequestHeader("Authorization") String token) {
//        String jwtToken = token.substring(7);
//        if(jwtService.isTokenInvalidated(jwtToken)){
//            return  ResponseEntity.status(404).body(null);
//        }
//        String username = jwtService.extractUsername(jwtToken);
//
//        if (username == null) {
//            return ResponseEntity.status(401).build();
//        }
//
//        boolean deleted = farmService.deleteFarm(id, username);
//        return deleted ? ResponseEntity.ok().build() : ResponseEntity.status(403).build(); // Forbidden if not the owner
//    }
//
//    // Example of token validation logic
//    private boolean isValidToken(String token) {
//        // Implement your token validation logic here
//        return true; // Placeholder for actual validation
//    }
//}
