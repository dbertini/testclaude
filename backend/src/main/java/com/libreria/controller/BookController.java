package com.libreria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller REST per gestione libri (CRUD)
 */
@RestController
@RequestMapping("/api/book")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * Lista libri (admin: tutti; user: solo propri con accessi)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listBooks(@RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();

        // Controlla se è admin o utente standard
        String userRole = bookService.getUserRole();

        if ("admin".equals(userRole)) {
            // Admin: legge tutti i libri
            List<Map<String, Object>> books = bookService.getBooksWithRelations();
            result.put("books", books);
            result.put("total", books.size());
            result.put("limit", limit);
        } else {
            // User: legge solo i propri libri con accessi validati
            List<Map<String, Object>> books = bookService.getMyBooksWithAccess();
            result.put("books", books);
            result.put("total", books.size());
            result.put("limit", limit);
        }

        return ResponseEntity.ok().body(result);
    }

    /**
     * Crea nuovo libro (senza attributo editore/autore esterno)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBook(@RequestBody BookRequest book) {
        // User non può aggiungere editore/autore esterno
        if (!"admin".equals(bookService.getUserRole()) &&
            book.getAutor_nominativo().length() > 0 ||
            book.getEditor_id().length() > 0) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Non puoi aggiungere autore o editore esterno"));
        }

        String id = bookService.createBook(book);
        return ResponseEntity.ok().body(Map.of("id", id, "message", "Libro creato con successo"));
    }

    /**
     * Cella libro (admin: cascata; user: solo i propri libri)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        // User non può eliminare libri di altri
        if (!"admin".equals(bookService.getUserRole()) &&
            !bookService.isMyBook(id)) {
            return ResponseEntity.status(403).body(Collections.singletonMap("error", "Non hai accesso per eliminare"));
        }

        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Aggiorna libro (admin: completo; user: solo campi di proprietà)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBook(@PathVariable String id, @RequestBody BookRequest book) {
        // User non può modificare autore/autore esterno o editore
        if (!"admin".equals(bookService.getUserRole()) &&
            (book.getAutor_nominativo().length() > 0 ||
             book.getEditor_id().length() > 0)) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Non puoi modificare autore o editore esterni"));
        }

        bookService.updateBook(id, book);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Libro aggiornato"));
    }

    /**
     * Cerca libro per titolo/autore
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBooks(@RequestBody SearchRequest request) {
        String query = request.getQuery();
        List<Map<String, Object>> books = bookService.searchBooksWithRelations(query);

        return ResponseEntity.ok().body(Map.of("results", books));
    }

    /**
     * Utile per BookRequest: dati libro senza autorizzazione esterna
     */
    private static class BookRequest {
        private String titolo_edizione;
        private int copie_possedute;
        private String annotazioni;

        public String getTitolo_edizione() { return titolo_edizione; }
        public void setTitolo_edizione(String titolo_edizione) { this.titolo_edizione = titolo_edizione; }
        public int getCopie_possedute() { return copie_possedute; }
        public void setCopie_possedute(int copie_possedute) { this.copie_possedute = copie_possedute; }
        public String getAnnotazioni() { return annotazioni; }
        public void setAnnotazioni(String annotazioni) { this.annotazioni = annotazioni; }
    }

    /**
     * Utile per SearchRequest: query di ricerca
     */
    private static class SearchRequest {
        private String query;

        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
    }
}
