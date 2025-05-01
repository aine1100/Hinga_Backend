package com.Hinga.farmMis.Config;

import com.stripe.Stripe;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    public StripeConfig(){
        Stripe.apiKey="";
    }
}
