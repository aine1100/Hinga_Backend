package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.services.JwtService;
import com.Hinga.farmMis.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    public OrderController(OrderService orderService, JwtService jwtService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

    @PostMapping("/create-from-cart")
    public ResponseEntity<?> createOrderFromCart(
            @RequestHeader("Authorization") String token,
            @RequestBody Orders request) {
        try {
            Long userId = extractUserId(token);
            System.out.println(request.toString());
            System.out.println(request.toString());


            // Validate cartId
            if (request.getCart() == null || request.getCart().getId() == 0) {
                return new ResponseEntity<>("Error: Cart ID is required", HttpStatus.BAD_REQUEST);
            }

            Orders createdOrder = orderService.createOrderFromCart(request);
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buyer")
    public ResponseEntity<?> getBuyerOrders(@RequestHeader("Authorization") String token) {
        try {
            Long userId = extractUserId(token);
            List<Orders> orders = orderService.getOrdersForBuyer(userId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/farmer")
    public ResponseEntity<?> getFarmerOrders(@RequestHeader("Authorization") String token) {
        try {
            Long userId = extractUserId(token);
            List<Orders> orders = orderService.getOrdersForFarmer(userId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Long extractUserId(String token) throws Exception {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new Exception("Invalid token format");
        }
        String jwtToken = token.substring(7);
        if (!jwtService.isTokenValid(jwtToken)) {
            throw new Exception("Invalid or expired token");
        }
        return jwtService.extractId(jwtToken);
    }
}