package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.AuthRequest;
import com.julietamarateo.photography.dto.AuthResponse;
import com.julietamarateo.photography.entity.User;
import com.julietamarateo.photography.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        User user = authService.getUserByEmail(email);

        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());
        data.put("role", user.getRole());
        data.put("id", user.getId());

        return ResponseEntity.ok(data);
    }
}
