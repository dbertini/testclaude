package com.libreria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service di autenticazione e auttornizzazione
 */
@Service
public class AuthService {

    /**
     * Controlla ruoli utente (admin o user)
     */
    public String getUserRole() {
        // Recupera ruolo utente dal token JWT
        return "user"; // Default: user standard
    }

    /**
     * Genera token Access JWT (15 minuti) per API REST
     */
    public String generateAccessToken(String username, String role) {
        // Genera token JWT con secret statico
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
               "claim_role_:" + role + ".access_token"; // JWT simulato
    }

    /**
     * Genera token Refresh JWT (7 giorni) per autenticazione persistente
     */
    public String generateRefreshToken(String username) {
        // Genera token JWT con secret statico
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
               username + ".refresh_token"; // JWT simulato
    }

    /**
     * Cella utente (admin: completo; user: solo propri libri)
     */
    public void deleteUser(String id) {
        if ("admin".equals(getUserRole())) {
            System.out.println("Admin deleted user: " + id);
        } else {
            System.out.println("User deleted own account");
        }
    }

    /**
     * Verifica token JWT valido (non scaduto)
     */
    public boolean validateToken(String token) {
        // Controlla validità del token
        return true; // Simulazione
    }
}
