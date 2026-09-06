package com.julietamarateo.photography.config;

import com.cloudinary.Cloudinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryConfig.class);

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        String name = cloudName != null ? cloudName.trim() : "";
        String key = apiKey != null ? apiKey.trim() : "";
        String secret = apiSecret != null ? apiSecret.trim() : "";

        if (name.isEmpty() || key.isEmpty() || secret.isEmpty()) {
            log.warn("Cloudinary: credenciales incompletas o vacías (cloud_name='{}'). El almacenamiento local operará como fallback seguro.",
                    name.isEmpty() ? "<no configurado>" : name);
        } else {
            log.info("Cloudinary: inicializado correctamente con cloud_name='{}'.", name);
        }

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", name);
        config.put("api_key", key);
        config.put("api_secret", secret);
        return new Cloudinary(config);
    }
}
