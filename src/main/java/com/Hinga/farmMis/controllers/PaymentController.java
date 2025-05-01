//package com.Hinga.farmMis.controllers;
//
//import com.Hinga.farmMis.services.PaymentService;
//import com.Hinga.farmMis.utils.Payments;
//import com.stripe.model.PaymentIntent;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/payments")
//public class PaymentController {
//
//    @Autowired
//    private PaymentService paymentService;
//
//    // Handle payment creation
//    @PostMapping("/create")
//    public Map<String, String> createPayment(@RequestBody Payments payments, @RequestParam double amount) throws Exception {
//        // Call the service to create a PaymentIntent
//        PaymentIntent paymentIntent = paymentService.createPaymentIntent(payments, amount);
//
//        // Prepare response map with necessary payment info
//        Map<String, String> response = new HashMap<>();
//        response.put("clientSecret", paymentIntent.getClientSecret()); // Client secret to complete payment on frontend
//        response.put("paymentIntentId", paymentIntent.getId()); // Payment Intent ID
//        response.put("customerName", payments.getCustomerName()); // Customer name (from Payments)
//        response.put("amount", String.valueOf(amount)); // Total amount
//        response.put("currency", paymentIntent.getCurrency()); // Currency
//        response.put("description", paymentIntent.getDescription()); // Description
//
//        return response;
//    }
//}
