package com.julietamarateo.photography.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long jwtExpirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long jwtExpirationMs) {
        
        SecretKey keyToUse;
        try {
            // Intentar decodificar como BASE64 primero
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            keyToUse = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            // Si no es base64, usar bytes directos UTF-8 asegurando longitud mínima
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            keyToUse = Keys.hmacShaKeyFor(keyBytes);
        }
        this.key = keyToUse;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token JWT inválido: " + e.getMessage());
        }
        return false;
    }
}
