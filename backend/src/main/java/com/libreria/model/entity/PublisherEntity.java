package com.libreria.model.entity;

import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity JPA per Case Editrici (anagrafica esterna)
 */
@Entity
@Table(name = "case_editrici")
public class PublisherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Denominazione della casa editrice
     */
    @Column(name = "denominazione", length = 255, nullable = false)
    private String denominazione;

    /**
     * Sede città della case editrice
     */
    @Column(name = "sede_citta", length = -1)
    private String sedeCitta;

    /**
     * Paese (código PA)
     */
    @Column(name = "p_a", length = 10)
    private String pA;

    /**
     * Anno di fondazione
     */
    @Column(name = "founded_year")
    private Integer foundedYear;

    /**
     * Specializzazioni
     */
    @Column(name = "specialt_e", length = -1)
    private String specialtE;

    public PublisherEntity() { }

    public PublisherEntity(String id, String denominazione, String sedeCitta, String pA, Integer foundedYear, String specialtE) {
        this.id = id;
        this.denominazione = denominazione;
        this.sedeCitta = sedeCitta;
        this.pA = pA;
        this.foundedYear = foundedYear;
        this.specialtE = specialtE;
    }

    // Getters e Setters per tutti i campi
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDenominazione() { return denominazione; }
    public void setDenominazione(String denominazione) { this.denominazione = denominazione; }

    public String getSedeCitta() { return sedeCitta; }
    public void setSedeCitta(String sedeCitta) { this.sedeCitta = sedeCitta; }

    public String getPA() { return pA; }
    public void setPA(String pA) { this.pA = pA; }

    public Integer getFoundedYear() { return foundedYear; }
    public void setFoundedYear(Integer foundedYear) { this.foundedYear = foundedYear; }

    public String getSpecialtE() { return specialtE; }
    public void setSpecialtE(String specialtE) { this.specialtE = specialtE; }

}
