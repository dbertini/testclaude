package com.libreria.model.entity;

import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity JPA per Posizioni geografiche (anagrafica esterna)
 */
@Entity
@Table(name = "posizione")
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Nome comune della posizione
     */
    @Column(name = "nome_comune", length = -1, nullable = false)
    private String nomeComune;

    /**
     * Paese (nome completo)
     */
    @Column(name = "paese", length = -1)
    private String paese;

    /**
     * Codice del Paese (código PA)
     */
    @Column(name = "codice_pa", length = 2)
    private String codicePA;

    /**
     * Natura della posizione (città, provincia, stato, altre)
     */
    @Column(name = "natura", length = 50)
    private String natura;

    public PositionEntity() { }

    public PositionEntity(String id, String nomeComune, String paese, String codicePA, String natura) {
        this.id = id;
        this.nomeComune = nomeComune;
        this.paese = paese;
        this.codicePA = codicePA;
        this.natura = natura;
    }

    // Getters e Setters per tutti i campi
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNomeComune() { return nomeComune; }
    public void setNomeComune(String nomeComune) { this.nomeComune = nomeComune; }

    public String getPaese() { return paese; }
    public void setPaese(String paese) { this.paese = paese; }

    public String getCodicePA() { return codicePA; }
    public void setCodicePA(String codicePA) { this.codicePA = codicePA; }

    public String getNatura() { return natura; }
    public void setNatura(String natura) { this.natura = natura; }

}
