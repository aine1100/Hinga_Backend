package com.Hinga.farmMis.Config;

import com.stripe.Stripe;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    public StripeConfig(){
        Stripe.apiKey="sk_test_51Oh5yiQnzXSA3KXtShx6AI2BWzWbUTjBTmHqCwnP8EDouKIp8C3027wPV4j6um6GLl4kS9RF2Q2obtmGihXQibQr00pQKWOH7Q";
    }
}
