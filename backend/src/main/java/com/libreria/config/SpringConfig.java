package com.libreria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureNames.HS256;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Configurazione Spring Boot con JWT e Password Encryption
 */
@Configuration
@Component
public class SpringConfig {

    /**
     * Encoderaggio password BCrypt (costante 10) per sicurezza
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10); // Costante per sicurezza
    }

    /**
     * Configurazione JWT con secret statico (genera random 32+ caratteri Hexadecimal)
     */
    private static String JWT_SECRET;

    static {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        char[] chars = '0123456789abcdefghijklmnopqrstuvwxyz';
        for (int i = 0; i < 32; i++) {
            sb.append(chars[random.nextInt(chars.length)]);
        }
        JWT_SECRET = sb.toString();
    }

    private static class JwtUtil {
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
                    .signWith(HS256, JWT_SECRET)
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
                    .signWith(HS256, JWT_SECRET)
                    .compact();
        }
    }
}
