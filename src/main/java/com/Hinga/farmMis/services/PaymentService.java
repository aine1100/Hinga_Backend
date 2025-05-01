//package com.Hinga.farmMis.services;
//
//import com.Hinga.farmMis.utils.Payments;
//import com.stripe.exception.StripeException;
//import com.stripe.model.PaymentIntent;
//import com.stripe.param.PaymentIntentCreateParams;
//import org.springframework.stereotype.Service;
//
//@Service
//public class PaymentService {
//
//    public PaymentIntent createPaymentIntent(Payments payments, double amount) throws StripeException {
//        // PaymentIntent expects amount in cents, so multiply by 100
//        long amountInCents = (long) (amount * 100);
//
//        // Set up the PaymentIntent creation parameters
//        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
//                .setAmount(amountInCents) // Stripe expects amount in cents
//                .setCurrency("usd") // Set currency to USD or allow dynamic change
//                .setDescription("Payment for Order") // Optional description
//                .setReceiptEmail(payments.getCustomerEmail()); // Optional, if you want to send a receipt email
//
//        // Add the payment method and customer information
//        if (payments.getPaymentMethodId() != null && !payments.getPaymentMethodId().isEmpty()) {
//            paramsBuilder.setPaymentMethod(payments.getPaymentMethodId());
//        }
//
//        // Set the customer ID if available (optional, Stripe can associate the payment with the customer)
//        // You may need to create a customer object separately and link the customer ID here
//        // Example: .setCustomer(customerId);
//
//        PaymentIntentCreateParams params = paramsBuilder.build();
//
//        // Create the PaymentIntent on Stripe
//        return PaymentIntent.create(params);
//    }
//}
