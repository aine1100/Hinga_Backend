package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Constants.PaymentStatus;
import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.Model.Payment;
import com.Hinga.farmMis.repository.OrderRepository;
import com.Hinga.farmMis.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        System.out.println("Stripe API Key initialized: " + (stripeApiKey != null ? "Key is set" : "Key is null"));
    }

    public Session createCheckoutSessionForOrder(Orders order, String successUrl, String cancelUrl) throws StripeException {
        // Debug logging
        System.out.println("=== Creating Stripe Checkout Session ===");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Order Status: " + order.getOrderStatus());
        System.out.println("Payment Status: " + order.getPaymentStatus());
        System.out.println("Number of carts: " + (order.getCarts() != null ? order.getCarts().size() : 0));
        
        if (order.getCarts() == null || order.getCarts().isEmpty()) {
            throw new IllegalArgumentException("Order has no carts");
        }
        
        Cart firstCart = order.getCarts().get(0);
        if (firstCart.getBuyer() == null) {
            throw new IllegalArgumentException("Cart has no buyer information");
        }
        
        String buyerEmail = firstCart.getBuyer().getEmail();
        System.out.println("Buyer Email: " + buyerEmail);
        if (buyerEmail == null || buyerEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer email is required for payment");
        }

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        
        // Calculate total amount and create line items
        double totalAmount = 0;
        for (Cart cart : order.getCarts()) {
            if (cart.getLivestock() == null) {
                throw new IllegalArgumentException("Cart has no livestock information");
            }
            
            double itemAmount = cart.getLivestock().getPrice() * cart.getQuantity();
            totalAmount += itemAmount;
            
            System.out.println("Processing cart item: " + cart.getLivestock().getType() + 
                             " - " + cart.getLivestock().getBreed() + 
                             ", Quantity: " + cart.getQuantity() + 
                             ", Price: " + cart.getLivestock().getPrice());
            
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) cart.getQuantity())
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount((long) (cart.getLivestock().getPrice() * 100)) // Convert to cents
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(cart.getLivestock().getType() + " - " + cart.getLivestock().getBreed())
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
            lineItems.add(lineItem);
        }

        System.out.println("Total Amount: " + totalAmount);
        System.out.println("Success URL: " + successUrl);
        System.out.println("Cancel URL: " + cancelUrl);
        System.out.println("================================");

        // Create Stripe checkout session
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .setCustomerEmail(buyerEmail)
                .putMetadata("orderId", order.getId().toString())
                .build();

        return Session.create(params);
    }

    @Transactional
    public Payment processPayment(String paymentIntentId, Long orderId) throws StripeException {
        // Retrieve the payment intent from Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        
        // Get the order
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Create payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setStripePaymentId(paymentIntentId);
        payment.setAmount(paymentIntent.getAmount() / 100.0); // Convert from cents
        payment.setCurrency(paymentIntent.getCurrency());
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());
        
        // Set payment method details if available
        if (paymentIntent.getPaymentMethod() != null) {
            String paymentMethodId = paymentIntent.getPaymentMethod().toString();
            payment.setPaymentMethod(paymentMethodId);
            
            // Retrieve payment method details
            com.stripe.model.PaymentMethod paymentMethod = com.stripe.model.PaymentMethod.retrieve(paymentMethodId);
            if (paymentMethod != null && paymentMethod.getCard() != null) {
                payment.setCardLast4(paymentMethod.getCard().getLast4());
                payment.setCardBrand(paymentMethod.getCard().getBrand());
            }
        }

        // Update order payment status
        order.setPaymentStatus(PaymentStatus.PAID);
        orderRepository.save(order);

        // Save payment record
        return paymentRepository.save(payment);
    }

    public Payment getPaymentByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.isEmpty() ? null : payments.get(0);
    }

    public Payment getPaymentByStripeId(String stripePaymentId) {
        return paymentRepository.findByStripePaymentId(stripePaymentId);
    }

    public PaymentIntent createPaymentIntent(Orders order) throws StripeException {
        // Calculate total amount
        double totalAmount = order.getCarts().stream()
                .mapToDouble(cart -> cart.getLivestock().getPrice() * cart.getQuantity())
                .sum();

        // Create payment intent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (totalAmount * 100)) // Convert to cents
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return PaymentIntent.create(params);
    }

    @Transactional
    public void handleSuccessfulCheckoutSession(Session session) {
        System.out.println("Entering handleSuccessfulCheckoutSession for Session ID: " + session.getId());
        // Retrieve order ID from session metadata
        String orderIdString = session.getMetadata() != null ? session.getMetadata().get("orderId") : null;

        if (orderIdString == null) {
            System.err.println("Error in webhook: Session metadata missing orderId. Session ID: " + session.getId());
            return;
        }

        System.out.println("Attempting to process Order ID from metadata: " + orderIdString);

        try {
            Long orderId = Long.parseLong(orderIdString);
            System.out.println("Parsed Order ID: " + orderId);

            Orders order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

            System.out.println("Found Order with ID: " + order.getId() + ", current status: " + order.getOrderStatus() + ", payment status: " + order.getPaymentStatus());

            // Update order payment status
            order.setPaymentStatus(PaymentStatus.PAID);
            orderRepository.save(order);

            System.out.println("Order " + orderId + " payment status updated to PAID.");

            // Create a Payment record
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setStripePaymentId(session.getPaymentIntent()); // Use PaymentIntent ID from the session
            payment.setAmount(session.getAmountTotal() / 100.0); // Use total amount from the session
            payment.setCurrency(session.getCurrency());
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaymentDate(LocalDateTime.now());

            // Save payment record
            paymentRepository.save(payment);
            System.out.println("Payment record created for order " + orderId + ". Payment ID: " + payment.getId());

        } catch (NumberFormatException e) {
            System.err.println("Error in webhook: Invalid orderId format in metadata: " + orderIdString);
        } catch (IllegalArgumentException e) {
            System.err.println("Error in webhook: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing successful checkout session for order ID " + orderIdString + ": " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Exiting handleSuccessfulCheckoutSession.");
    }
}
