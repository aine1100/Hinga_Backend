//package com.Hinga.farmMis.controllers;
//import com.Hinga.farmMis.Constants.LivestockStatus;
//import com.Hinga.farmMis.Model.Livestock;
//import com.Hinga.farmMis.services.JwtService;
//;
//import com.Hinga.farmMis.services.farmer.LivestockService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/livestock")
//public class LivestockController {
//    private LivestockService livestockService;
//    private JwtService jwtService;
//
//    public LivestockController(LivestockService livestockService,JwtService jwtService){
//        this.livestockService = livestockService;
//        this.jwtService = jwtService;
//
//    }
//
//    @PostMapping("/create/{farmId}")
//    public ResponseEntity<Livestock> createLivestock(@RequestHeader("Authorization") String token,@RequestBody Livestock livestock, @PathVariable Long farmId){
//        String jwtToken = token.substring(7); // Remove "Bearer " prefix
//        if(jwtService.isTokenInvalidated(jwtToken)){
//            return  ResponseEntity.status(404).body(null);
//        }
//        String username = jwtService.extractUsername(jwtToken);
//
//
//
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//        ResponseEntity<Livestock> livestock1=ResponseEntity.ok(livestockService.addLivestock(livestock,farmId));
//            return new ResponseEntity<>(livestock1.getBody(), HttpStatus.CREATED) ;
//    }
//
//    @GetMapping("/{farmId}")
//
//    public ResponseEntity<Optional<List<Livestock>>> getLivestock(@PathVariable Long farmId, @RequestHeader("Authorization") String token){
//        String jwtToken = token.substring(7);
//        if(jwtService.isTokenInvalidated(jwtToken)){
//            return  ResponseEntity.status(404).body(null);
//        }
//        String username = jwtService.extractUsername(jwtToken);
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//        ResponseEntity<Optional<List<Livestock>>> livestockList=ResponseEntity.ok(livestockService.getLivestock(farmId));
//        return (livestockList !=null) ? livestockList:ResponseEntity.status(404).build();
//
//
//    }
//
//    @GetMapping("/{farmId}/{Id}")
//
//    public ResponseEntity<Livestock> getLivestockById(@PathVariable Long farmId, @PathVariable Long Id,@RequestHeader("Authorization") String token){
//        String jwtToken = token.substring(7);
//        if(jwtService.isTokenInvalidated(jwtToken)){
//            return  ResponseEntity.status(404).body(null);
//        }
//        String username = jwtService.extractUsername(jwtToken);
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//        ResponseEntity<Livestock> livestock1=ResponseEntity.ok(livestockService.getLiveStockById(Id,farmId));
//        return (livestock1 !=null) ? livestock1:ResponseEntity.status(404).build();
//    }
//
//    @PutMapping("/{farmId}/{Id}")
//    public ResponseEntity<Livestock> updateLivestock(@RequestHeader("Authorization") String token,@PathVariable Long farmId,@PathVariable Long Id,@RequestBody Livestock livestock){
//        String jwtToken = token.substring(7);
//        if(jwtService.isTokenInvalidated(jwtToken)){
//            return  ResponseEntity.status(404).body(null);
//        }
//        String username = jwtService.extractUsername(jwtToken);
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//        ResponseEntity<Livestock> updatedLivestock=ResponseEntity.ok(livestockService.updateLiveStock(farmId,Id,livestock));
//        return (updatedLivestock !=null) ? updatedLivestock:ResponseEntity.status(404).build();
//    }
//
//    @DeleteMapping("/{farmId}/{Id}")
//
//    public ResponseEntity<Void> deleteLivestock(@PathVariable Long farmId,@PathVariable Long Id,@RequestHeader("Authorization") String token) throws IOException {
//        String jwtToken = token.substring(7);
//        if(jwtService.isTokenInvalidated(jwtToken)){
//            return  ResponseEntity.status(404).body(null);
//        }
//        String username = jwtService.extractUsername(jwtToken);
//        if (username == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//        boolean deleted=livestockService.deleteLivestock(farmId,Id);
//        return deleted ? ResponseEntity.status(200).build() : ResponseEntity.status(404).build();
//
//
//    }
//
//}
