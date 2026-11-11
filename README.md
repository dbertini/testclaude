# 📚 Libreria Personale

Sito web responsive per gestire la libreria personale con backend REST in Java Spring Boot e frontend statico HTML/JavaScript.

## 🎯 Scopo

Gestione di una libreria personale con due livelli di accesso:
- **Admin**: vede e gestisce tutto (tutle le entità)
- **Utente standard**: vede e gestisce solo i propri libri (crea, aggiorna, cella solo libri associati)

Il backend espone API REST future usabile da app iOS.

## 🚀 Avvio Quick Start

### Prerequisiti

```bash
# Java 21+ per backend
java --version

# Node.js LTS per frontend
node --version

# Docker runtime (opzionale ma consigliato)
docker --version
```

### Avvio Backend (Java/Spring Boot)

**Opzione 1: Maven**
```bash
cd backend && mvn clean package -DskipTests
cd .. && java -jar backend/target/libreria-api-1.0.0-SNAPSHOT.jar
```

**Opzione 2: Docker (consigliato)**
```bash
# Avvio completo stack
docker compose up --build
```

### Avvio Frontend (HTML/JS)

**Opzione 1: Nginx + Docker**
```bash
cd .. && docker compose up frontend
```

**Opzione 2: Browser statico**
```bash
# Copia file a dist/ o serving locale
cp -r public/* dist/
open dist/index.html
```

### Avvio Infrastructure Docker (Consigliato)

```bash
# Prerequisiti
docker --version

# Avvio completo stack con healthcheck
docker compose up --build

# Controllare servizi
docker compose ps
docker compose logs --tail=50 backend

# Ingressi a backend
docker run -p 8080:8080 libreria_backend:latest sh -c "curl http://localhost:8080"

# Ingressi a frontend
docker run -p 3000:8080 libreria_frontend:latest sh -c "open http://localhost:3000"
```

## 📁 Struttura Progetto

```
libreria-personale/
├── backend/                    # API REST in Java Spring Boot
│   ├── src/main/java/com/libreria/
│   │   ├── config/          # Configurazione Spring + JWT
│   │   ├── controller/      # Controller REST API
│   │   ├── service/         # Business logic
│   │   ├── repository/      @SpringDataJPA
│   │   ├── model/entity/    Entity JPA
│   │   ├── model/dto/       Data Transfer Objects
│   │   └── exception/        Gestione errori
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── bootstrap.properties
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                  # Interfaccia web HTML/JS/CSS
│   ├── src/css/              Fogli di stile responsive
│   ├── src/js/               Logica JavaScript
│   ├── public/                Risorse statiche
│   │   └── index.html
│   ├── nginx.conf
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml         # Avvio completa stack containerizzata
├── postgres/
│   └── init.sql              # Schema PostgreSQL iniziale
├── docs/
│   └── PIANO.md              # Piano di implementazione completo
└── README.md                  # Questo file
```

## 🔧 Comandi Sviluppo

### Backend - Maven Build & Run

```bash
# Installare dipendenze e build
cd backend && mvn clean package -DskipTests

# Esecuzione server (Maven)
cd .. && java -jar backend/target/*.jar

# Avvio debug per test
mvn exec:java -Dexec.mainClass="com.libreria.config.SpringConfig"
```

### Frontend - Build Statico

```bash
# Installare dipendenze frontend
cd frontend/ && npm install

# Serve file statici con Nginx (Docker)
docker compose up --build frontend

# Serve file statici manualmente
cp -r public/* dist/
open dist/index.html
```

### Infrastruttura Docker

```bash
# Avvio completo stack (PostgreSQL + Backend + Frontend)
docker compose up --build

# Controllare healthcheck PostgreSQL
docker compose ps

# Log backend e frontend
docker compose logs -f backend
docker compose logs -f frontend
```

## 🗄️ Database PostgreSQL

### Struttura dati (schema)

```sql
-- Tabella utenti con ruoli di accesso
CREATE TABLE utente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE,
    password_hash BYTEA NOT NULL,
    role CHAR(1) NOT NULL CHECK (role IN ('admin', 'user')),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Tabella libri con anagrafica autore e case editrice
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

-- Tabella anagrafica autore (esterna)
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
    founded_year INT CHECK (founded_year > 1900 OR founded_year IS NULL)
);

-- Tabella anagrafica posizioni
CREATE TABLE posizione (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_comune TEXT NOT NULL,
    paese TEXT,
    codice_pa CHAR(2),
    natura VARCHAR(50) CHECK (natura IN ('città', 'provincia', 'stato', 'altre'))
);

-- Relazioni: autore e libro (many-to-many)
CREATE TABLE libri_autori (
    libro_id UUID REFERENCES libro(id) ON DELETE CASCADE,
    autore_id UUID REFERENCES autori(id),
    PRIMARY KEY (libro_id, autore_id)
);

-- Relazioni: case editrici e posizion (many-to-one)
CREATE TABLE case_editrici_posizione (
    case_editrice_id UUID REFERENCES case_editrici(id) ON DELETE CASCADE,
    posizione_id UUID REFERENCES posizion(id) ON DELETE CASCADE,
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

-- Index per performance
CREATE INDEX idx_libro_titolo_autore ON libro(titolo_edizione, autore_nominativo);
```

### Installazione Database Iniziale

```bash
# Avvia container PostgreSQL con schema iniziale
docker compose up postgres --build

# Connettione al database (PostgreSQL 16+ con pg_isready)
PGPASSWORD=${POSTGRES_PASSWORD} psql -U libreria -l
PGPASSWORD=${POSTGRES_PASSWORD} psql -U libreria -c "\dt"

# Importa schema iniziale se necessario
docker compose run --service postgres sh -c "cat ./postgres/init.sql" | psql postgres
```

## 📡 API REST Principali

### Autenticazione e Autorizzazione

| Endpoint | Metodo | Ruolo | Descrizione |
|----------|--------|-------|-------------|
| `/api/auth/login` | POST | user, admin | Login che restituisce token access e refresh |
| `/api/auth/logout` | POST | user, admin | Logout che cancella token e sessione |

### Gestione Libri (CRUD)

| Endpoint | Metodo | Ruolo | Operazioni |
|----------|--------|-------|------------|
| `/api/book` | GET | Admin, User | Legge tutti i libri (admin) o solo propri (user) |
| `/api/book/{id}` | GET | Admin, User | Legge un libro esistente |
| `/api/book` | POST | Admin, User | Crea un nuovo libro (senza attributo esterno) |
| `/api/book/{id}` | PUT | Admin, User | Aggiorna il libro (admin: completo; user: solo propri) |
| `/api/book/{id}` | DELETE | Admin, User | Cella il libro (admin: cascata; user: solo i propri) |

### Gestione Autore (Esterno)

| Endpoint | Metodo | Ruolo | Operazioni |
|----------|--------|-------|------------|
| `/api/author` | GET | Admin, User | Legge gli autori esistenti |
| `/api/author/{id}` | GET | Admin, User | Cerca autore per nome o ID |
| `/api/author` | POST | Admin | Crea un nuovo autore (senza libro iniziale) |
| `/api/author/{id}` | PUT | Admin | Aggiorna l'autore esistente |
| `/api/author/{id}` | DELETE | Admin | Cancella autore con cascata su libri |

### Gestione Case Editrici

| Endpoint | Metodo | Ruolo | Operazioni |
|----------|--------|-------|------------|
| `/api/publisher` | GET | Admin, User | Legge le case editrici esistenti |
| `/api/publisher/{id}` | GET | Admin, User | Cerca editore per nome o ID |
| `/api/publisher` | POST | Admin | Crea un nuovo editore (senza libro) |
| `/api/publisher/{id}` | PUT | Admin | Aggiorna l'editore esistente |
| `/api/publisher/{id}` | DELETE | Admin | Cancella editore con cascata su libri |

### Gestione Posizioni

| Endpoint | Metodo | Ruolo | Operazioni |
|----------|--------|-------|------------|
| `/api/position` | GET | Admin, User | Legge le posizioni esistenti |
| `/api/position/{id}` | GET | Admin, User | Cerca posizione per nome o ID |
| `/api/position` | POST | Admin | Crea una nuova posizione |
| `/api/position/{id}` | PUT | Admin | Aggiorna la posizione esistente |
| `/api/position/{id}` | DELETE | Admin | Cancella posizione con cascata su libri |

### Gestione Utente

| Endpoint | Metodo | Ruolo | Operazioni |
|----------|--------|-------|------------|
| `/api/user/me` | GET | Admin, User | Legge i dati utente e permessi di accesso |
| `/api/user/me` | PUT | Admin, User | Aggiorna nome utente e password (senza esporre password) |

## 🔐 Sicurezza - Autenticazione JWT

- **Access Token**: 15 minuti (refresh token) per operazioni API
- **Refresh Token**: 7 giorni per autenticazione persistente
- **Logout**: cancella sia il refresh token che annulla l'access token su server

## 📚 Struttura Dati e Relazioni

```
utente (1) → molti libro         : utente ha accesso a libri del proprio dominio
libro (1) → 1 autore             : relazione m:1 (ogni autore può esser citato in libri di un medesimo editore, ma l'inverso non è vero)
libro (1) → 1 casa_editrice      : una case editrice gestisce le opere dei propri autori
autori (1) → molti libro         : un autore ha scritto diversi titoli, ciascuno con una differente edizione
case_editrici (1) → 1 posizione   : ogni editore è ubicato in una determinata sede
posizione (1) → 1 casa_editrice  : una sola sede per case editrice
```

## 🧪 Test e Verifiche

### Backend - Build & Run

```bash
cd backend && mvn clean test
```

### Frontend - Serve Statico

```bash
docker compose up --build frontend
```

## 🔍 Verifiche End-to-End

1. **Avvio Backend**: `docker compose up backend`
2. **Connettione Database**: `docker compose ps postgres`
3. **Ingressi API REST**: `curl http://localhost:8080/api/auth/login`
4. **Ingressi Frontend**: `open http://localhost:3000`

## 📝 Note Importanti

- `.env`: NON SCRIPTARE! Contiene secret (password, JWT secret)
- `.gitignore`: Include `.env`, `target/`, `dist/`, log file
- **Genera secret con comando**: `openssl rand -hex 32` o `RANDOM_HEX_32_CARATTARI_HEX_DELL'
