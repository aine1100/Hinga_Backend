//package com.Hinga.farmMis.controllers;
//
//import com.Hinga.farmMis.Model.Crops;
//import com.Hinga.farmMis.services.CropService;
//import com.Hinga.farmMis.services.JwtService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/crops")
//@CrossOrigin(origins = "http://localhost:5173")
//public class CropController {
//
//    @Autowired
//    private CropService cropService;
//    private JwtService jwtService;
//
//    public CropController(CropService cropService, JwtService jwtService) {
//        this.cropService = cropService;
//        this.jwtService = jwtService;
//    }
//
//    @PostMapping("/create")
//    public ResponseEntity<Crops> createCrop(@RequestHeader("Authorization") String token,@RequestBody Crops crops, @RequestParam Long farmId) {
//        String jwtToken = token.substring(7); // Remove "Bearer " prefix
//        String username = jwtService.extractUsername(jwtToken);
//
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//        crops.setFarmDate(LocalDate.now());
//        Crops createCrop= cropService.createCrop(crops, farmId);
//        return new ResponseEntity<>(createCrop, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<List<Crops>> getAllCrops(@RequestHeader("Authorization") String token,@RequestParam Long farmId) {
//        String jwtToken = token.substring(7); // Remove "Bearer " prefix
//        String username = jwtService.extractUsername(jwtToken);
//
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//        List<Crops> getAll= cropService.getCrops(farmId);
//        return (getAll!=null) ? ResponseEntity.ok(getAll):ResponseEntity.status(404).build();
//    }
//
//    @GetMapping("/{farmId}/{cropId}")
//    public ResponseEntity<Optional<Crops>>  getCropById(@RequestHeader("Authorization") String token,@PathVariable Long farmId,@PathVariable Long cropId) {
//
//        String jwtToken = token.substring(7); // Remove "Bearer " prefix
//        String username = jwtService.extractUsername(jwtToken);
//
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//        Crops getCrops= cropService.getCropById(farmId, cropId).orElseThrow(() -> new RuntimeException("Crop not found!"));
//        return (getCrops !=null) ? ResponseEntity.ok(Optional.of(getCrops)):ResponseEntity.status(404).build() ;
//    }
//
//    @PutMapping("/update/{farmId}/{cropId}")
//    public ResponseEntity<Crops>  updateCrop(@RequestHeader("Authorization") String token,@PathVariable Long cropId,@RequestBody Crops crop,@PathVariable Long farmId){
//        String jwtToken = token.substring(7); // Remove "Bearer " prefix
//        String username = jwtService.extractUsername(jwtToken);
//
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//        Crops updatedCrops= cropService.UpdateCrop(crop, farmId, cropId);
//        return (updatedCrops !=null) ? ResponseEntity.ok(updatedCrops):ResponseEntity.status(404).build();
//    }
//
//    @DeleteMapping("/delete/{farmId}/{cropId}")
//    public ResponseEntity<Void> deleteCrop(@RequestHeader("Authorization") String token,@PathVariable Long cropId, @PathVariable Long farmId){
//        String jwtToken = token.substring(7); // Remove "Bearer " prefix
//        String username = jwtService.extractUsername(jwtToken);
//
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//        boolean deleted= cropService.deleteCrop(farmId,cropId);
//        return deleted ? ResponseEntity.ok().build() : ResponseEntity.status(404).build();
//    }
//}
