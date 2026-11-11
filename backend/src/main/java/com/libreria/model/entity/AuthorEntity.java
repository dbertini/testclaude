package com.libreria.model.entity;

import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity JPA per Autore (anagrafica esterna)
 */
@Entity
@Table(name = "autori")
public class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Nome completo dell'autore/grato
     */
    @Column(name = "nome_completo", length = 255, nullable = false)
    private String nomeCompleto;

    /**
     * Anno di nascita
     */
    @Column(name = "nascita")
    private Integer nascita;

    /**
     * Anno di morte (nullable)
     */
    @Column(name = "morte", length = 32, nullable = true)
    private Integer morte;

    public AuthorEntity() { }

    public AuthorEntity(String id, String nomeCompleto, Integer nascita, Integer morte) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.nascita = nascita;
        this.morte = morte;
    }

    // Getters e Setters per tutti i campi
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public Integer getNascita() { return nascita; }
    public void setNascita(Integer nascita) { this.nascita = nascita; }

    public Integer getMorte() { return morte; }
    public void setMorte(Integer morte) { this.morte = morte; }

}
