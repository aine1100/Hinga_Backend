package com.Hinga.farmMis.Config;

import com.stripe.Stripe;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    public StripeConfig(){
        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY"); // Or hardcode for testing: "sk_test_..."
    }
}
