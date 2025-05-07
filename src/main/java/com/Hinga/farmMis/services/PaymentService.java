package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Cart;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {
    public Session createCheckoutSessionForOrder(Orders order, String successUrl, String cancelUrl) throws StripeException {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (Cart cart : order.getCarts()) {
            long unitAmount = (long) (cart.getLivestock().getPrice() * 100); // Stripe expects cents
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) cart.getQuantity())
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(unitAmount)
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
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .build();
        return Session.create(params);
    }
}
