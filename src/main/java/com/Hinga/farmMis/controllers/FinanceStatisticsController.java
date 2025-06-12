package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Constants.UserRoles;
import com.Hinga.farmMis.Model.FinanceStatistics;
import com.Hinga.farmMis.services.FinanceStatisticsService;
import com.Hinga.farmMis.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class FinanceStatisticsController {

    @Autowired
    private FinanceStatisticsService financeStatisticsService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/statistics")
    public ResponseEntity<?> saveTransaction(@RequestBody FinanceStatistics statistics, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);

            if (role != UserRoles.FARMER) {
                return new ResponseEntity<>("Unauthorized: Only farmers can access this endpoint", HttpStatus.FORBIDDEN);
            }
            return ResponseEntity.ok(financeStatisticsService.saveTransaction(statistics));
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<?> getFarmerStatistics(@PathVariable Long farmerId, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);

            if (role != UserRoles.FARMER) {
                return new ResponseEntity<>("Unauthorized: Only farmers can access this endpoint", HttpStatus.FORBIDDEN);
            }
            if (!userId.equals(farmerId)) {
                return new ResponseEntity<>("Unauthorized: Cannot access another farmer's data", HttpStatus.FORBIDDEN);
            }
            return ResponseEntity.ok(financeStatisticsService.getFarmerStatistics(farmerId));
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/farmer/{farmerId}/monthly-revenue")
    public ResponseEntity<?> getMonthlyRevenue(@PathVariable Long farmerId, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);

            if (role != UserRoles.FARMER) {
                return new ResponseEntity<>("Unauthorized: Only farmers can access this endpoint", HttpStatus.FORBIDDEN);
            }
            if (!userId.equals(farmerId)) {
                return new ResponseEntity<>("Unauthorized: Cannot access another farmer's data", HttpStatus.FORBIDDEN);
            }
            return ResponseEntity.ok(financeStatisticsService.getMonthlyRevenue(farmerId));
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/farmer/{farmerId}/category-revenue")
    public ResponseEntity<?> getProductCategoryRevenue(@PathVariable Long farmerId, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);

            if (role != UserRoles.FARMER) {
                return new ResponseEntity<>("Unauthorized: Only farmers can access this endpoint", HttpStatus.FORBIDDEN);
            }
            if (!userId.equals(farmerId)) {
                return new ResponseEntity<>("Unauthorized: Cannot access another farmer's data", HttpStatus.FORBIDDEN);
            }
            return ResponseEntity.ok(financeStatisticsService.getProductCategoryRevenue(farmerId));
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/farmer/{farmerId}/quantity-sold")
    public ResponseEntity<?> getProductQuantitySold(@PathVariable Long farmerId, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);

            if (role != UserRoles.FARMER) {
                return new ResponseEntity<>("Unauthorized: Only farmers can access this endpoint", HttpStatus.FORBIDDEN);
            }
            if (!userId.equals(farmerId)) {
                return new ResponseEntity<>("Unauthorized: Cannot access another farmer's data", HttpStatus.FORBIDDEN);
            }
            return ResponseEntity.ok(financeStatisticsService.getProductQuantitySold(farmerId));
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/farmer/{farmerId}/quarterly-revenue")
    public ResponseEntity<?> getQuarterlyRevenue(@PathVariable Long farmerId, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);

            if (role != UserRoles.FARMER) {
                return new ResponseEntity<>("Unauthorized: Only farmers can access this endpoint", HttpStatus.FORBIDDEN);
            }
            if (!userId.equals(farmerId)) {
                return new ResponseEntity<>("Unauthorized: Cannot access another farmer's data", HttpStatus.FORBIDDEN);
            }
            return ResponseEntity.ok(financeStatisticsService.getQuarterlyRevenue(farmerId));
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/farmer/{farmerId}/expenses")
    public ResponseEntity<?> getTotalExpenses(@PathVariable Long farmerId, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);

            System.out.println("Token: " + jwtToken);
            System.out.println("Role: " + role);
            System.out.println("User ID: " + userId);
            System.out.println("Requested Farmer ID: " + farmerId);

            if (role != UserRoles.FARMER) {
                System.out.println("Forbidden: User is not a FARMER");
                return new ResponseEntity<>("Unauthorized: Only farmers can access this endpoint", HttpStatus.FORBIDDEN);
            }
            if (!userId.equals(farmerId)) {
                System.out.println("Forbidden: User ID does not match Farmer ID");
                return new ResponseEntity<>("Unauthorized: Cannot access another farmer's data", HttpStatus.FORBIDDEN);
            }
            return ResponseEntity.ok(financeStatisticsService.getTotalExpenses(farmerId));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new ResponseEntity<>("Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}