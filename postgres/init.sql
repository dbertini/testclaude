-- =====================================
-- Schema Iniziale Libreria Personale
-- PostgreSQL 16+ (PostgreSQLDialect)
-- =====================================

SET client_encoding = 'UTF8';

-- Creazione database se non esiste
CREATE DATABASE libreria_db;

-- Connettione al database (se già creato)
\c libreria_db;

-- =====================================
-- TABELLE E RELAZIONI
-- =====================================

-- Tabella utenti con ruoli di accesso
CREATE TABLE utente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE,
    password_hash BYTEA NOT NULL,
    role CHAR(1) NOT NULL CHECK (role IN ('admin', 'user')),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Tabella libri con anagrafica autore e casa editrice
CREATE TABLE libro (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    autore_nominativo VARCHAR(255) NOT NULL,
    titolo_edizione TEXT NOT NULL,
    copie_possedute INTEGER DEFAULT 0 CHECK (copie_possedute >= 0),
    annotazioni TEXT,
    data_acquisto DATE,
    deleted_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    FOREIGN KEY (autore_nominativo) REFERENCES autori(nome_completo) ON DELETE CASCADE,
    FOREIGN KEY (editore_id) REFERENCES case_editrici(id) ON DELETE SET NULL
);

-- Tabella anagrafica autore/grato (esterna)
CREATE TABLE autori (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_completo VARCHAR(255) NOT NULL,
    nascita INT CHECK (nascite >= 1900 OR nascita IS NULL),
    morte INT CHECK (morte > nascita OR morte IS NULL OR morte IS NULL)
);

-- Tabella anagrafica case editrici
CREATE TABLE case_editrici (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    denominazione VARCHAR(255) NOT NULL,
    sede_citta TEXT,
    p_a VARCHAR(10),
    founded_year INT CHECK (founded_year > 1900 OR founded_year IS NULL),
    specialt_e TEXT
);

-- Tabella anagrafica posizioni
CREATE TABLE posizione (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_comune TEXT NOT NULL,
    paese TEXT,
    codice_pa CHAR(2) CHECK (codice_pa IN ('IT', 'FR', 'DE', 'ES', 'NL', 'AT', 'CH', 'IE')),
    natura VARCHAR(50) CHECK (natura IN ('città', 'provincia', 'stato', 'altre'))
);

-- Relazioni: autore e libro (many-to-many)
CREATE TABLE libri_autori (
    libro_id UUID REFERENCES libro(id) ON DELETE CASCADE,
    autore_id UUID REFERENCES autori(id),
    PRIMARY KEY (libro_id, autore_id)
);

-- Relazioni: case editrici e posizione (many-to-one)
CREATE TABLE case_editrici_posizione (
    case_editrice_id UUID REFERENCES case_editrici(id) ON DELETE CASCADE,
    posizione_id UUID REFERENCES posizione(id) ON DELETE CASCADE,
    PRIMARY KEY (case_editrice_id, posizione_id)
);

-- Relazioni: casa editrice e libro (many-to-many)
CREATE TABLE libri_case_editrici (
    libro_id UUID REFERENCES libro(id) ON DELETE CASCADE,
    case_editrice_id UUID REFERENCES case_editrici(id) ON DELETE SET NULL,
    PRIMARY KEY (libro_id, case_editrice_id)
);

-- Relazioni: utente e libro con permessi di accesso
CREATE TABLE accesso_libri (
    utente_id UUID REFERENCES utente(id) ON DELETE CASCADE,
    libro_id UUID REFERENCES libro(id) ON DELETE CASCADE,
    domain_type VARCHAR(20) NOT NULL CHECK (domain_type IN ('libri', 'editrici', 'autori')),
    PRIMARY KEY (utente_id, libro_id, domain_type)
);

-- Index di ricerca su titolo ed autore
CREATE INDEX idx_libro_titolo_autore ON libro(titolo_edizione, autore_nominativo);

-- Indichazioni per performance
CREATE INDEX idx_authori_nome ON autori(nome_completo);
CREATE INDEX idx_editrici_denominazione ON case_editrici(denominazione);
CREATE INDEX idx_posizioni_nome ON posizione(nome_comune);

-- =====================================
-- DATI DI DEMO INIZIALI (opzionale)
-- =====================================

INSERT INTO utente (username, email, password_hash, role) VALUES
('admin', 'admin@libreria.it', gen_random_text_bytes(32), 'admin'),
('utente1', 'utente1@test.com', gen_random_text_bytes(32), 'user');

INSERT INTO case_editrici (denominazione, sede_citta, p_a, founded_year) VALUES
('Edizioni Italiane', 'Roma', 'IT', 1950);

INSERT INTO posizione (nome_comune, paese, codice_pa, natura) VALUES
('Roma', 'Italia', 'IT', 'città');

-- =====================================
-- FINALIZZAZIONE
-- =====================================

\quit;
