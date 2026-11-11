package com.libreria.model.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object per libro con dati correlati (autore, editore, posizione)
 */
public class BookWithRelations {

    private String id;
    private String autoreNominativo;
    private String titoloEdizione;
    private String editoreId;
    private String posizioneId;
    private String isbn10;  // ISBN prima edizione (formato 10)
    private String isbn13;  // ISBN seconda edizione (formato 13)
    private int copiePossedute;
    private String annotazioni;
    private LocalDateTime dataAcquisto;
    private String authorName; // Nome completo autore (da autori.)
    private String publisherName; // Denominazione casa editrice (da case_editrici.)
    private String positionName; // Nome comune posizione (da posizione.)

    public BookWithRelations(String id, String autoreNominativo, String titoloEdizione) {
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

    public String getIsbn10() { return isbn10; }
    public void setIsbn10(String isbn10) { this.isbn10 = isbn10; }

    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }

    public String getEditoreId() { return editoreId; }
    public void setEditoreId(String editoreId) { this.editoreId = editoreId; }

    public String getPosizioneId() { return posizioneId; }
    public void setPosizioneId(String posizioneId) { this.posizioneId = posizioneId; }

    public int getCopiePossedute() { return copiePossedute; }
    public void setCopiePossedute(int copiePossedute) { this.copiePossedute = copiePossedute; }

    public String getAnnotazioni() { return annotazioni; }
    public void setAnnotazioni(String annotazioni) { this.annotazioni = annotazioni; }

    public LocalDateTime getDataAcquisto() { return dataAcquisto; }
    public void setDataAcquisto(LocalDateTime dataAcquisto) { this.dataAcquisto = dataAcquisto; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }
}
