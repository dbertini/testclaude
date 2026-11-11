package com.libreria.config;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Utile JWT per autenticazione con Spring Security JWT
 */
@Service
public class JwtUtil {

    private static String JWT_SECRET = "genera_random_hex_32_caratteri";

    /**
     * Genera token Access (15 minuti) per API REST
     */
    public String generateAccessToken(String username, String role) {
        long expiryDate = System.currentTimeMillis() + 15 * 60 * 1000L; // 15 min

        return Jwts.builder()
                .subject(username)
                .claims().claim("role", role)
                .issuedAt(new Date())
                .expiresAt(new Date(expiryDate))
                .signWith(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.HS256)
                .compact();
    }

    /**
     * Genera token Refresh (7 giorni) per autenticazione persistente
     */
    public String generateRefreshToken(String username) {
        long expiryDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L; // 7 day

        return Jwts.builder()
                .subject(username)
                .claims().claim("role", "user")
                .issuedAt(new Date())
                .expiresAt(new Date(expiryDate))
                .signWith(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.HS256)
                .compact();
    }

    /**
     * Verifica se token è valido (non scaduto)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
