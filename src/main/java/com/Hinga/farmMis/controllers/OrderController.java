package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Constants.OrderStatus;
import com.Hinga.farmMis.Constants.UserRoles;
import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.services.JwtService;
import com.Hinga.farmMis.services.OrderService;
import com.Hinga.farmMis.Dto.Request.MultiCartOrderRequest;
import com.Hinga.farmMis.Dto.Request.OrderRequest;
import com.Hinga.farmMis.Dto.response.FarmerOrderSummary;
import com.Hinga.farmMis.Dto.response.OrderResponse;
import com.Hinga.farmMis.services.PaymentService;
import com.stripe.model.checkout.Session;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;
import com.Hinga.farmMis.services.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;
    private final PaymentService paymentService;
    private final PdfService pdfService;
    private OrderStatus status;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    public OrderController(OrderService orderService, JwtService jwtService, PaymentService paymentService, PdfService pdfService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
        this.paymentService = paymentService;
        this.pdfService = pdfService;
    }

    @PostMapping("/create-from-cart")
    public ResponseEntity<?> createOrderFromCart(
            @RequestHeader("Authorization") String token,
            @RequestBody OrderRequest request) {
        try {
            Long userId = extractUserId(token);

            // Extract JWT token properly
            String jwtToken = token.substring(7); // Remove "Bearer " prefix

            // Extract buyer information from token
            String firstName = jwtService.extractFirstName(jwtToken);
            String lastName = jwtService.extractLastName(jwtToken);
            String buyerPhone = jwtService.extractPhoneNumber(jwtToken);

            // Validate extracted values
            if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
                return new ResponseEntity<>("Error: Invalid or missing buyer name in token", HttpStatus.BAD_REQUEST);
            }
            if (buyerPhone == null || buyerPhone.trim().isEmpty()) {
                return new ResponseEntity<>("Error: Invalid or missing buyer phone in token", HttpStatus.BAD_REQUEST);
            }

            String buyerName = firstName.trim() + " " + lastName.trim();

            // Debug logging
            System.out.println("=== Token Information ===");
            System.out.println("JWT Token: " + jwtToken);
            System.out.println("Buyer Name from token: " + buyerName);
            System.out.println("Buyer Phone from token: " + buyerPhone);
            System.out.println("=======================");

            // Validate request
            if (request.getSelectedCartIds() == null || request.getSelectedCartIds().isEmpty()) {
                return new ResponseEntity<>("Error: At least one cart ID must be selected", HttpStatus.BAD_REQUEST);
            }

            if (request.getDeliveryAddress() == null ||
                    request.getDeliveryAddress().getProvince() == null || request.getDeliveryAddress().getProvince().trim().isEmpty() ||
                    request.getDeliveryAddress().getDistrict() == null || request.getDeliveryAddress().getDistrict().trim().isEmpty() ||
                    request.getDeliveryAddress().getSector() == null || request.getDeliveryAddress().getSector().trim().isEmpty() ||
                    request.getDeliveryAddress().getCell() == null || request.getDeliveryAddress().getCell().trim().isEmpty() ||
                    request.getDeliveryAddress().getVillage() == null || request.getDeliveryAddress().getVillage().trim().isEmpty()) {
                return new ResponseEntity<>("Error: All delivery address fields are required", HttpStatus.BAD_REQUEST);
            }

            // Create Orders object from request
            Orders order = new Orders();
            order.setDeliveryDate(request.getDeliveryDate() != null ? request.getDeliveryDate() : LocalDate.now().plusDays(7));
            order.setDeliveryAddress(request.getDeliveryAddress());
            order.setBuyerName(buyerName);
            order.setBuyerPhone(buyerPhone);

            // Debug logging for order object
            System.out.println("=== Order Object Before Service ===");
            System.out.println("Order ID: " + order.getId());
            System.out.println("Order Buyer Name: " + order.getBuyerName());
            System.out.println("Order Buyer Phone: " + order.getBuyerPhone());
            System.out.println("Order Status: " + order.getOrderStatus());
            System.out.println("================================");

            OrderResponse createdOrder = orderService.createOrderFromSelectedCarts(order, request.getSelectedCartIds(), userId);
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

    @PostMapping("/{orderId}/pay-after-approval")
    public ResponseEntity<?> payAfterApproval(@RequestHeader("Authorization") String token, @PathVariable Long orderId) {
        try {
            Long userId = extractUserId(token);
            Orders order = orderService.getOrderById(orderId);
            
            // Verify order status
            if (order.getOrderStatus() != OrderStatus.APPROVED) {
                return new ResponseEntity<>("Order must be approved before payment", HttpStatus.BAD_REQUEST);
            }            
            // Only allow buyer to pay
            Long buyerId = order.getCarts().get(0).getBuyer().getId();
            if (!userId.equals(buyerId)) {
                return new ResponseEntity<>("Unauthorized: Only the buyer can pay for this order", HttpStatus.FORBIDDEN);
            }
            
            // Create checkout session and append orderId to successUrl
            String finalSuccessUrl = successUrl + "&orderId=" + orderId;
            Session session = paymentService.createCheckoutSessionForOrder(order, finalSuccessUrl, cancelUrl);
            return new ResponseEntity<>(session.getUrl(), HttpStatus.OK);
        } catch (StripeException e) {
            return new ResponseEntity<>("Stripe error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            // Create checkout session and append orderId to successUrl
            String finalSuccessUrl = successUrl + "&orderId=" + orderId;
            Session session = paymentService.createCheckoutSessionForOrder(order, finalSuccessUrl, cancelUrl);
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

    @GetMapping("/farmer/download")
    public ResponseEntity<?> downloadFarmerOrders(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            UserRoles role = jwtService.extractRole(jwtToken);
            Long userId = jwtService.extractId(jwtToken);
            
            if (role != UserRoles.FARMER) {
                return new ResponseEntity<>("Unauthorized: Only farmers can access this endpoint", HttpStatus.FORBIDDEN);
            }
            
            // Get farmer's name for the PDF title
            String farmerName = jwtService.extractFirstName(jwtToken) + " " + jwtService.extractLastName(jwtToken);
            
            // Get orders
            List<Orders> orders = orderService.getOrdersForFarmer(userId);
            
            // Generate PDF
            byte[] pdfBytes = pdfService.generateOrdersPdf(orders, farmerName);
            
            // Create filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "orders_" + farmerName.replace(" ", "_") + "_" + timestamp + ".pdf";
            
            // Set headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
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