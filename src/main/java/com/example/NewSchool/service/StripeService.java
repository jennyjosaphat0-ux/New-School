package com.example.NewSchool.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    // Kreye yon Stripe Checkout Session
    public Session createCheckoutSession(String description, BigDecimal montantGdes,
                                          String eleveId, String typePaiement) throws Exception {
        Stripe.apiKey = secretKey;

        // Konvèti Gdes → Santim (Stripe travay ak santim)
        // 1000 gdes = 100000 santim (x100)
        long montantSantim = montantGdes.multiply(BigDecimal.valueOf(100)).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(cancelUrl)
            .addLineItem(SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("htg") // Gourde Haitienne
                    .setUnitAmount(montantSantim)
                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(description)
                        .setDescription("NewScool - Paiement Scolarite")
                        .build())
                    .build())
                .build())
            .putMetadata("eleveId", eleveId)
            .putMetadata("typePaiement", typePaiement)
            .build();

        return Session.create(params);
    }

    // Verifye yon session Stripe
    public Session retrieveSession(String sessionId) throws Exception {
        Stripe.apiKey = secretKey;
        return Session.retrieve(sessionId);
    }
}
