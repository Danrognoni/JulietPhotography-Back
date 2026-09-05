package com.julietamarateo.photography.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfig {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoConfig.class);

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        if (accessToken != null && !accessToken.isBlank() && !accessToken.contains("XXXX")) {
            com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
            log.info("Mercado Pago SDK inicializado con Access Token configurado.");
        } else {
            // Inicializar con token placeholder si no está configurado para evitar null pointer
            com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken != null ? accessToken : "TEST-PLACEHOLDER");
            log.warn("Mercado Pago SDK inicializado con credencial de prueba o placeholder.");
        }
    }

    public String getAccessToken() {
        return accessToken;
    }
}
