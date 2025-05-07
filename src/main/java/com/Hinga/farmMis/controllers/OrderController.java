package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Constants.UserRoles;
import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.services.JwtService;
import com.Hinga.farmMis.services.OrderService;
import com.Hinga.farmMis.Dto.Request.MultiCartOrderRequest;
import com.Hinga.farmMis.Dto.response.FarmerOrderSummary;
import com.Hinga.farmMis.Dto.response.OrderResponse;
import com.Hinga.farmMis.services.PaymentService;
import com.stripe.model.checkout.Session;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, JwtService jwtService, PaymentService paymentService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
        this.paymentService = paymentService;
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
            if (request.getCarts() == null || request.getCarts().isEmpty() || request.getCarts().get(0) == null || request.getCarts().get(0).getId() == 0) {
                return new ResponseEntity<>("Error: Cart ID is required", HttpStatus.BAD_REQUEST);
            }

            OrderResponse createdOrder = orderService.createOrderFromCart(request);
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
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);
            if (role ==UserRoles.FARMER) {
                // If farmer, redirect to farmer orders
                List<Orders> orders = orderService.getOrdersForFarmer(userId);
                return new ResponseEntity<>(orders, HttpStatus.OK);
            } else {
                // Otherwise, treat as buyer
                List<Orders> orders = orderService.getOrdersForBuyer(userId);
                return new ResponseEntity<>(orders, HttpStatus.OK);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/farmer")
    public ResponseEntity<?> getFarmerOrders(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);
            
            if (role != UserRoles.FARMER) {
                return new ResponseEntity<>("Unauthorized: Only farmers can access this endpoint", HttpStatus.FORBIDDEN);
            }
            
            List<Orders> orders = orderService.getOrdersForFarmer(userId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{orderId}/approve")
    public ResponseEntity<?> approveOrder(@RequestHeader("Authorization") String token, @PathVariable Long orderId) {
        try {
            Long farmerId = extractUserId(token);
            Orders approvedOrder = orderService.approveOrder(orderId, farmerId);
            return new ResponseEntity<>(approvedOrder, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@RequestHeader("Authorization") String token, @PathVariable Long orderId) {
        try {
            Long farmerId = extractUserId(token);
            Orders cancelledOrder = orderService.cancelOrder(orderId, farmerId);
            return new ResponseEntity<>(cancelledOrder, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@RequestHeader("Authorization") String token, @PathVariable Long orderId) {
        try {
            // Only allow buyer or farmer to view order details
            Long userId = extractUserId(token);
            Orders order = orderService.getOrderById(orderId);
            Long buyerId = order.getCarts().get(0).getBuyer().getId();
            Long farmerId = order.getCarts().get(0).getLivestock().getFarmer().getId();
            if (!userId.equals(buyerId) && !userId.equals(farmerId)) {
                return new ResponseEntity<>("Unauthorized: You do not have access to this order", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-from-carts")
    public ResponseEntity<?> createOrderFromCarts(
            @RequestHeader("Authorization") String token,
            @RequestBody MultiCartOrderRequest request) {
        try {
            Long userId = extractUserId(token);
            OrderResponse createdOrder = orderService.createOrderFromCarts(userId, request);
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<?> payForOrder(@RequestHeader("Authorization") String token, @PathVariable Long orderId) {
        try {
            Long userId = extractUserId(token);
            Orders order = orderService.getOrderById(orderId);
            // Only allow buyer to pay
            Long buyerId = order.getCarts().get(0).getBuyer().getId();
            if (!userId.equals(buyerId)) {
                return new ResponseEntity<>("Unauthorized: Only the buyer can pay for this order", HttpStatus.FORBIDDEN);
            }
            String successUrl = "https://yourdomain.com/payment-success?session_id={CHECKOUT_SESSION_ID}";
            String cancelUrl = "https://yourdomain.com/payment-cancel";
            Session session = paymentService.createCheckoutSessionForOrder(order, successUrl, cancelUrl);
            return new ResponseEntity<>(session.getUrl(), HttpStatus.OK);
        } catch (StripeException e) {
            return new ResponseEntity<>("Stripe error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/farmer/summary")
    public ResponseEntity<?> getFarmerOrderSummary(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);
            
            if (role != UserRoles.FARMER) {
                return new ResponseEntity<>("Unauthorized: Only farmers can access this endpoint", HttpStatus.FORBIDDEN);
            }
            
            FarmerOrderSummary summary = orderService.getOrderSummaryForFarmer(userId);
            return new ResponseEntity<>(summary, HttpStatus.OK);
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