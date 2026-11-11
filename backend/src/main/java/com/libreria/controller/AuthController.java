package com.libreria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller REST per autenticazione e autorizzazione
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserRequest user) {
        // Salva username e password nell'utente nel database
        String userId = authService.login(user.getUsername(), user.getPassword());

        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Usero non trovato"));
        }

        // Genera token JWT per access e refresh
        String accessToken = authService.generateAccessToken(userId, authService.getRole(userId));
        String refreshToken = authService.generateRefreshToken(userId);

        return ResponseEntity.ok().body(Map.of(
            "access_token", accessToken,
            "refresh_token", refreshToken
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Cancelli token refresh e cancelli sessione lato client
        authService.logout();
        return ResponseEntity.noContent().build();
    }

    /**
     * Utile per login: Username o email
     */
    private static class UserRequest {
        private String username;
        private String password;

        public UserRequest() {}

        public UserRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
