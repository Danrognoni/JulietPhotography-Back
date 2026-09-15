package com.julietamarateo.photography.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint liviano y público de keep-alive para monitores externos como UptimeRobot o health checks de Render.
 * No requiere token JWT ni autenticación y responde de inmediato con HTTP 200 OK.
 */
@RestController
@CrossOrigin(origins = "*")
public class PingController {

    @GetMapping(value = {"/ping", "/api/ping"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("status", "UP", "message", "pong"));
    }
}
