package com.julietamarateo.photography;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JulietPhotographyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JulietPhotographyApplication.class, args);
    }

    /**
     * Endpoint liviano para keep-alive / health checks en Render.
     * Responde de inmediato con HTTP 200 y texto plano "OK" sin consultar la base de datos.
     */
    @GetMapping(value = {"/health", "/api/health"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

}

