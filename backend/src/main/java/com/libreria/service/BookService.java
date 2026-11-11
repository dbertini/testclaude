package com.libreria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service di business per gestione libri e permessi utente
 */
@Service
public class BookService {

    /**
     * Controlla ruoli utente (admin o user)
     */
    public String getUserRole() {
        // Recupera ruolo utente dal token JWT
        return "user"; // Default: user standard
    }

    /**
     * Verifica se un libro è associato all'utente corrente
     */
    public boolean isMyBook(String id) {
        // Controlla accessi nel database
        return true; // Simulazione
    }

    /**
     * Genera token Access JWT (15 minuti) per API REST
     */
    public String generateAccessToken(String username, String role) {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
               "claim_role_:" + role + ".access_token"; // JWT simulato
    }

    /**
     * Genera token Refresh JWT (7 giorni) per autenticazione persistente
     */
    public String generateRefreshToken(String username) {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
               username + ".refresh_token"; // JWT simulato
    }

    /**
     * Crea nuovo libro (senza attributo editore/autore esterno)
     */
    public String createBook(BookRequest book) {
        // Genera ID UUID random
        return "lib-" + newUUID();
    }

    /**
     * Aggiorna libro esistente
     */
    public void updateBook(String id, BookRequest book) {
        // Update nel database (simulato)
    }

    /**
     * Cella libro (admin: cascata; user: solo i propri libri)
     */
    public void deleteBook(String id) {
        if ("admin".equals(getUserRole())) {
            // Admin: elimina libro e tutte le relazioni (cascata)
            System.out.println("Admin deleted book: " + id);
        } else {
            // User: solo i propri libri con accessi
            if (!isMyBook(id)) {
                throw new RuntimeException("Non hai accesso per eliminare");
            }
            System.out.println("User deleted book: " + id);
        }
    }

    /**
     * Genera ID UUID random (simulato)
     */
    private static String newUUID() {
        return java.util.UUID.randomUUID().toString();
    }
}
