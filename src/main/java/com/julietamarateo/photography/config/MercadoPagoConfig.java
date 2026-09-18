package com.julietamarateo.photography.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfig {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoConfig.class);

    @Value("${mercadopago.access-token:APP_USR-2055087385616169-091722-2885b50d8f32ede52bebee30d01cb93e-3666810022}")
    private String accessToken;

    @Value("${mercadopago.public-key:APP_USR-c427100c-ad91-4947-b7cc-751e1235f15d}")
    private String publicKey;

    @Value("${mercadopago.collector-id:3666810022}")
    private String collectorId;

    @PostConstruct
    public void init() {
        if (accessToken != null && !accessToken.isBlank() && !accessToken.contains("XXXX")) {
            com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken.trim());
            log.info("Mercado Pago SDK inicializado exitosamente con Access Token (Collector ID: {}).", collectorId);
        } else {
            // Inicializar con token placeholder si no está configurado para evitar null pointer
            com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken != null ? accessToken.trim() : "TEST-PLACEHOLDER");
            log.warn("Mercado Pago SDK inicializado con credencial de prueba o placeholder.");
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getCollectorId() {
        return collectorId;
    }
}
