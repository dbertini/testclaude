package com.libreria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA per autore (Author Entity)
 */
@Repository
public interface AuthorRepo extends JpaRepository<com.libreria.model.entity.AuthorEntity, String> {

    /**
     * Cerca autori per nome con correlazioni complete
     */
    @Query("SELECT new com.libreria.model.dto.AuthorSummaryResponse(a.id, a.nomeCompleto, a.nascita, a.morte) " +
           "FROM com.libreria.model.entity.AuthorEntity a " +
           "WHERE LOWER(a.nomeCompleto) LIKE LOWER(%:query%) " +
           "ORDER BY a.nomeCompleto")
    List<AuthorSummaryResponse> searchAuthors(@Param("query") String query);

    /**
     * Verifica se autore è collegato ai libri del corrente utente
     */
    @Query("SELECT DISTINCT new com.libreria.model.dto.AuthorSummaryResponse(a.id, a.nomeCompleto, a.nascita, a.morte) " +
           "FROM com.libreria.model.entity.AuthorEntity a " +
           "JOIN JOIN com.libreria.model.entity.LibriAutori la " +
           "WHERE a = la.autore_id AND EXISTS (SELECT 1 FROM com.libreria.model.entity.Libro l WHERE l.id = la.libro_id)")
    List<AuthorSummaryResponse> findAuthorsAssociatedToBooks();

    /**
     * Cerca autore per ID
     */
    Optional<AuthorEntity> findById(String id);

}
