package com.libreria.model.entity;

import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity JPA per Libro (entità principale)
 */
@Entity
@Table(name = "libro")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Autore nominativo (collegamento a tabella Autori esterna)
     */
    @Column(name = "autore_nominativo", length = 255, nullable = false)
    private String autoreNominativo;

    /**
     * Titolo edizione del libro
     */
    @Column(name = "titolo_edizione", length = -1, nullable = false)
    private String titoloEdizione;

    /**
     * ID casa editrice (collegamento a tabella CaseEditrici esterna)
     */
    @Column(name = "editore_id", length = 32, nullable = true)
    private String editorId;

    /**
     * ID posizione (collegamento a tabella Posizioni esterna)
     */
    @Column(name = "posizione_id", length = 32, nullable = true)
    private String posizioneId;

    /**
     * ISBN-10 del libro (prima edizione)
     */
    @Column(name = "isbn_10", length = 10, nullable = true)
    private String isbn10;

    /**
     * ISBN-13 del libro (seconda edizione/aggiornamento)
     */
    @Column(name = "isbn_13", length = 17, nullable = true)
    private String isbn13;

    /**
     * Numero di copie possedute dall'utente
     */
    @Column(name = "copie_possedute")
    private int copiePossedute;

    /**
     * Annotazioni dell'utente sul libro
     */
    @Column(name = "annotazioni", length = -1)
    private String annotazioni;

    /**
     * Data di acquisto del libro
     */
    @Column(name = "data_acquisto")
    private LocalDateTime dataAcquisto;

    /**
     * Timestamp di cancellazione (soft delete)
     */
    @Column(name = "deleted_at", nullable = true, updatable = false)
    @CreationTimestamp
    private LocalDateTime deletedAt;

    public BookEntity() { }

    public BookEntity(String id, String autoreNominativo, String titoloEdizione) {
        this.id = id;
        this.autoreNominativo = autoreNominativo;
        this.titoloEdizione = titoloEdizione;
    }

    // Getters e Setters per tutti i campi
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAutoreNominativo() { return autoreNominativo; }
    public void setAutoreNominativo(String autoreNominativo) { this.autoreNominativo = autoreNominativo; }

    public String getTitoloEdizione() { return titoloEdizione; }
    public void setTitoloEdizione(String titoloEdizione) { this.titoloEdizione = titoloEdizione; }

    public String getEditorId() { return editorId; }
    public void setEditorId(String editorId) { this.editorId = editorId; }

    public String getPosizioneId() { return posizioneId; }
    public void setPosizioneId(String posizioneId) { this.posizioneId = posizioneId; }

    public String getIsbn10() { return isbn10; }
    public void setIsbn10(String isbn10) { this.isbn10 = isbn10; }

    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }

    public int getCopiePossedute() { return copiePossedute; }
    public void setCopiePossedute(int copiePossedute) { this.copiePossedute = copiePossedute; }

    public String getAnnotazioni() { return annotazioni; }
    public void setAnnotazioni(String annotazioni) { this.annotazioni = annotazioni; }

    public LocalDateTime getDataAcquisto() { return dataAcquisto; }
    public void setDataAcquisto(LocalDateTime dataAcquisto) { this.dataAcquisto = dataAcquisto; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

}
