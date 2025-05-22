package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Payment;
import com.Hinga.farmMis.services.OrderService;
import com.Hinga.farmMis.services.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.exception.SignatureVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Autowired
    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @PostMapping("/create-checkout-session/{orderId}")
    public ResponseEntity<?> createCheckoutSession(@PathVariable Long orderId) {
        try {
            Orders order = orderService.getOrderById(orderId);
            Session session = paymentService.createCheckoutSessionForOrder(order, successUrl, cancelUrl);
            
            Map<String, String> responseData = new HashMap<>();
            responseData.put("sessionId", session.getId());
            responseData.put("url", session.getUrl());
            
            return ResponseEntity.ok(responseData);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Error creating checkout session: " + e.getMessage());
        }
    }

    @PostMapping("/create-payment-intent/{orderId}")
    public ResponseEntity<?> createPaymentIntent(@PathVariable Long orderId) {
        try {
            Orders order = orderService.getOrderById(orderId);
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(order);
            
            Map<String, String> responseData = new HashMap<>();
            responseData.put("clientSecret", paymentIntent.getClientSecret());
            
            return ResponseEntity.ok(responseData);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Error creating payment intent: " + e.getMessage());
        }
    }

    @PostMapping("/process-payment")
    public ResponseEntity<?> processPayment(
            @RequestParam String paymentIntentId,
            @RequestParam Long orderId) {
        try {
            Payment payment = paymentService.processPayment(paymentIntentId, orderId);
            return ResponseEntity.ok(payment);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Error processing payment: " + e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/stripe/{stripePaymentId}")
    public ResponseEntity<?> getPaymentByStripeId(@PathVariable String stripePaymentId) {
        Payment payment = paymentService.getPaymentByStripeId(stripePaymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event = null;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            System.out.println("Received webhook event of type: " + event.getType());
        } catch (SignatureVerificationException e) {
            // Invalid signature
            System.out.println("Webhook Error: Invalid signature.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Use 400 for invalid signature
        } catch (Exception e) {
            // Generic error during construction/initial parsing
            System.err.println("Webhook Error during event construction: " + e.getMessage());
             e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // Handle the event
        switch (event.getType()) {
            case "checkout.session.completed":
                System.out.println("Processing checkout.session.completed event.");
                try {
                    // Deserialize the specific data object for this event type
                    Session session = (Session) event.getData().getObject();
                    if (session == null) {
                         System.err.println("Webhook Error: Session data object is null for checkout.session.completed event.");
                         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                    }
                    // Call the service method to handle successful checkout
                    paymentService.handleSuccessfulCheckoutSession(session);
                } catch (Exception e) {
                     System.err.println("Error handling checkout.session.completed event: " + e.getMessage());
                     e.printStackTrace();
                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
                break;
            // ... handle other event types
            case "payment_intent.succeeded":
                 System.out.println("Processing payment_intent.succeeded event.");
                try {
                    // Deserialize the specific data object for this event type
                    PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
                     if (paymentIntent == null) {
                         System.err.println("Webhook Error: PaymentIntent data object is null for payment_intent.succeeded event.");
                         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                    }
                 // While payment_intent.succeeded can also indicate success, for Stripe Checkout
                 // checkout.session.completed is usually the primary event to act upon.
                 // If you have other payment flows, you might need to handle this event too.
                 // For this Checkout flow, we will rely on checkout.session.completed.
                 System.out.println("Payment Intent succeeded event received, ID: " + paymentIntent.getId() + ". Relying on checkout.session.completed for order status update.");
                 // If you needed to process payment intents directly, you would call a service method here:
                 // paymentService.processPaymentIntentSucceeded(paymentIntent);
                } catch (Exception e) {
                     System.err.println("Error handling payment_intent.succeeded event: " + e.getMessage());
                     e.printStackTrace();
                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
                 break;
            case "charge.succeeded":
                 System.out.println("Ignoring charge.succeeded event for Checkout flow.");
                 // Charge.succeeded is often redundant when using Checkout, as checkout.session.completed covers it.
                 // You might handle this for other payment methods.
                 break;
             case "payment_intent.created":
                 System.out.println("Ignoring payment_intent.created event for Checkout flow.");
                 // This event occurs early in the payment process, before success.
                 break;
              case "charge.updated":
                 System.out.println("Ignoring charge.updated event for Checkout flow.");
                 // This event tracks changes to a charge.
                  break;
            default:
                // Unexpected event type
                System.out.println("Unhandled event type: " + event.getType());
                // Optionally log the full event payload for unhandled types
                // System.out.println("Event details: " + payload);
                break;
        }

        // Return a 200 response for all successfully received and handled (or ignored) events
        return ResponseEntity.ok("Success");
    }
}
