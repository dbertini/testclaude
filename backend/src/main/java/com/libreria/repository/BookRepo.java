package com.libreria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA per libri (Book Entity)
 */
@Repository
public interface BookRepo extends JpaRepository<com.libreria.model.entity.BookEntity, String> {

    /**
     * Cerca libri per titolo o autore con correlazioni esterne
     */
    @Query("SELECT new com.libreria.model.dto.BookSummaryResponse(b.id, b.titoloEdizione, b.autoreNominativo, b.editoreId, b.posizioneId, b.copiePossedute, b.annotazioni, b.dataAcquisto) " +
           "FROM com.libreria.model.entity.BookEntity b " +
           "WHERE (b.titoloEdizione LIKE %:query% OR b.autoreNominativo LIKE %:query%) " +
           "AND b.deletedAt IS NULL")
    List<BookSummaryResponse> searchBooks(@Param("query") String query);

    /**
     * Cerca libri con correlazioni complete (autore, editore, posizione)
     */
    @Query("SELECT new com.libreria.model.dto.BookWithRelations(b.id, b.titoloEdizione, b.autoreNominativo, b.editoreId, b.posizioneId, b.copiePossedute, b.annotazioni, " +
           "a.nomeCompleto, p.denominazione, ed.sedeCitta) " +
           "FROM com.libreria.model.entity.BookEntity b " +
           "WHERE b.deletedAt IS NULL " +
           "ORDER BY b.titoloEdizione")
    List<BookWithRelations> findBooksWithFullRelations();

}
