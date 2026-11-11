# Piano di Implementazione Libreria Personale

Questo documento descrive l'architettura completa del sistema per la gestione della tua libreria personale, con infrastructure containerizzata (Docker).

## 1. Modello dei Dati (Tabelle e Relazioni)

### **Tabelle Principali**

| Tabella | Descrizione | Campi chiave |
|---------|-------------|--------------|
| `utente` | Utenti registrati | `id`, `username`, `email`, `password_hash`, `role`, `created_at` |
| `libro` | Entità principale | `id`, `autore_nominativo`, `titolo_edizione`, `editore_id`, `posizione`, `copie_possedute`, `annotazioni`, `data_acquisto`, `deleted_at` |
| `autori` | Anagrafica autore/grato | `id`, `nome_completo`, `nascita`, `morte` (nullable) |
| `case_editrici` | Edizioni | `id`, `denominazione`, `sede_citta`, `p_a` (nullable), `founded_year`, `specialt_e` (text) |
| `posizione` | Anagrafica sede | `id`, `nome_comune`, `paese`, `codice_pa`, `natura` |

### **Relazioni**

```
utente (1) → molti libro         : utente ha accesso a libri del proprio dominio
libro (1) → 1 autore             : relazione m:1 (ogni autore può esser citato in libri di un medesimo editore, ma l'inverso non è vero)
libro (1) → 1 casa_editrice      : una casa editrice gestisce le opere dei propri autori
autori (1) → molti libro         : un autore ha scritto diversi titoli, ciascuno con una differente edizione
case_editrici (1) → 1 posizione   : ogni editore è ubicato in una determinata sede
posizione (1) → 1 casa_editrice  : una sola sede per casa editrice
```

### **Schema SQL PostgreSQL**

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

-- Tabella libri con anagrafica autore e casa editrice
CREATE TABLE libro (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    autore_nominativo VARCHAR(255) NOT NULL,
    titolo_edizione TEXT NOT NULL,
    copie_possedute INTEGER DEFAULT 0,
    annotazioni TEXT,
    data_acquisto DATE,
    deleted_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    FOREIGN KEY (autore_nominativo) REFERENCES autori(nome_completo),
    FOREIGN KEY (editore_id) REFERENCES case_editrici(id)
);

-- Tabella anagrafica autore (esterna)
CREATE TABLE autori (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_completo VARCHAR(255) NOT NULL,
    nascita INT,
    morte INT CHECK (morte > nascita OR morte IS NULL)
);

-- Tabella anagrafica case editrici
CREATE TABLE case_editrici (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    denominazione VARCHAR(255) NOT NULL,
    sede_citta TEXT,
    p_a VARCHAR(10),
    founded_year INT,
    specialt_e TEXT
);

-- Tabella anagrafica posizioni
CREATE TABLE posizione (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_comune TEXT NOT NULL,
    paese TEXT,
    codice_pa CHAR(2),
    natura VARCHAR(50) CHECK (natura IN ('città', 'provincia', 'stato', 'altre'))
);

-- Relazioni: autore e libro
CREATE TABLE libri_autori (
    libro_id UUID REFERENCES libro(id) ON DELETE CASCADE,
    autore_id UUID REFERENCES autori(id),
    PRIMARY KEY (libro_id, autore_id)
);

-- Relazioni: case editrici e posizione
CREATE TABLE case_editrici_posizione (
    case_editrice_id UUID REFERENCES case_editrici(id) ON DELETE CASCADE,
    posizione_id UUID REFERENCES posizione(id) ON DELETE CASCADE,
    PRIMARY KEY (case_editrice_id, posizione_id)
);

-- Relazioni: casa editrice e libro
CREATE TABLE libri_case_editrici (
    libro_id UUID REFERENCES libro(id) ON DELETE CASCADE,
    case_editrice_id UUID REFERENCES case_editrici(id),
    PRIMARY KEY (libro_id, case_editrice_id)
);

-- Relazioni: utente e libro con permessi di accesso
CREATE TABLE accesso_libri (
    utente_id UUID REFERENCES utente(id) ON DELETE CASCADE,
    libro_id UUID REFERENCES libro(id),
    domain_type VARCHAR(20) NOT NULL CHECK (domain_type IN ('libri', 'editrici', 'autori')),
    PRIMARY KEY (utente_id, libro_id, domain_type)
);

-- Index di ricerca su titolo ed autore
CREATE INDEX idx_libro_titolo_autore ON libro(titolo_edizione, autore_nominativo);
```

## 2. Struttura delle Cartelle

### **Backend** (Maven/Java Spring Boot + Docker)

```
backend/
├── src/main/java/com/libreria/
│   ├── config/               # Configurazione Spring e sicurezza
│   │   └── SecurityConfig.java
│   ├── controller/           # Controller REST API
│   │   ├── AuthController.java      (login, logout, registrazione)
│   │   ├── BookController.java      (gestione libri)
│   │   ├── AuthorController.java    (gestione autore)
│   │   ├── PublisherController.java (gestione editore)
│   │   └── PositionController.java  (gestione posizione)
│   ├── service/              # Business logic
│   │   ├── AuthService.java         (validazione credential, JWT)
│   │   ├── BookService.java        (business book, permessi utente)
│   │   ├── AuthorService.java       (gestione autore, recupero)
│   │   ├── PublisherService.java    (gestione editore, ricerca)
│   │   └── PositionService.java     (gestione posizione)
│   ├── repository/            # Repository dati PostgreSQL
│   │   ├── UserRepo.java           (@SpringDataJPA + query custom)
│   │   ├── BookRepo.java
│   │   ├── AuthorRepo.java
│   │   ├── PublisherRepo.java
│   │   └── PositionRepo.java
│   ├── model/                # Entità JPA e DTO
│   │   ├── entity/                 # Entity del database
│   │   │   ├── UserEntity.java
│   │   │   ├── BookEntity.java
│   │   │   ├── AuthorEntity.java
│   │   │   ├── PublisherEntity.java
│   │   │   └── PositionEntity.java
│   │   └── dto/                    # Data Transfer Objects
│   │       ├── LoginRequest.java
│   │       ├── UserResponse.java
│   │       └── BookSummaryResponse.java
│   ├── repository/              # Custom query (non @SpringDataJPA)
│   │   ├── AuthorRepoCustom.java
│   │   └── PositionRepoCustom.java
│   ├── service/                 (simile a service/)
│   │   ├── AuthServiceCustom.java
│   │   └── BookServiceCustom.java
│   ├── exception/               # Gestione errori
│   │   ├── BusinessException.java
│   │   └── global_exception_handler.java
│   └── utils/                   # Utili e validazione input
│       └── InputValidatorUtils.java
├── src/main/resources/         # Configurazione
│   ├── application.properties    (configurazione Spring)
│   └── db/                      # SQL script migration
│       └── init.sql
├── src/test/java/com/libreria/  # Test backend
│   └── ...
├── .env.example                 # Configurazione da versionare
├── pom.xml                     # Dipendenze Maven
└── Dockerfile                   # Build multi-stage backend
```

### **Frontend** (HTML/CSS/JS Responsive + Docker)

```
frontend/
├── src/
│   ├── css/                    # Fogli di stile e layout responsive
│   │   ├── base.css            # Variabili CSS, reset, core style
│   │   ├── components.css       # Stile componenti UI
│   │   └── responsive.css       # Media queries e breakpoint
│   └── js/                    # Logica JavaScript
│       ├── auth.js             (gestione sessione JWT)
│       ├── book.js             (CRUD libri, permessi utente)
│       ├── author.js           (gestione autore)
│       ├── publisher.js         (gestione editore)
│       └── position.js          (gestione posizione)
├── public/                     # Risorse statiche da servire su nginx
│   ├── index.html              # L'interfaccia principale con routing client-side
│   ├── manifest.json           # Metadati PWA opzionali
│   └── robots.txt              # Ottimizzazione SEO
├── nginx.conf                  # Configurazione server web nginx
├── docker-entrypoint.sh        # Script di avvio init per frontend
└── README.md                   # Istruzioni deployment frontend
```

### **Docker/Compose** (Infrastruttura containerizzata)

```
docker/                           # Containerizzazione completa
├── backend.dockerfile           # Build multi-stage Java + JRE leggero
├── nginx.conf                   # Configurazione server web nginx
└── docker-compose.yml           # Avvio completo stack multi-container

.env.example                     # Variabili d'ambiente (da versionare)
.gitignore                        # Escludere .env locale
```

### **Documentazione** (Sviluppo)

```
docs/
├── PIANO.md                    # Questo file - piano di implementazione completo
├── api/                        # Specifiche API OpenAPI/Swagger
│   ├── v1/                     # Version 1 di specifiche API
│   │   ├── openapi.yaml        # Formato OpenAPI 3.0
│   │   └── endpoints_overview.md # Overview API e endpoint principali
│   └── authentication.md       # Specifiche autenticazione (JWT, ruoli)
├── database/                   # Schema e documenti SQL
│   ├── schema.sql              # Definizione completa DB PostgreSQL
│   └── seed_data.sql           (opzionale: dati di esempio)
└── README.md
```

## 3. Configurazione Docker

### **Backend: Dockerfile multi-stage**

```dockerfile
# Fase 1: Build con Maven (immagine pesante)
FROM maven:openjdk-21-slim AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:copy-classfile -DoutputDirectory=/app/lib/
COPY src/main/java ./src/main/java
RUN mvn compile -DskipTests

# Fase 2: Runtime leggero (solo JRE)
FROM eclipse-temurin:21-jammy-slim
WORKDIR /app
COPY --from=builder lib/* ./lib/
COPY --from=builder target/*.jar .
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
EXPOSE 8080
CMD ["java", "$JAVA_OPTS", "-jar", "spring-boot.jar"]
```

### **Frontend: Dockerfile + Nginx**

```dockerfile
# Base Nginx image
FROM nginx:alpine

# Copia configurazione e contenuti statici
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
COPY ./frontend/public/ /usr/share/nginx/html/
COPY ./frontend/src/ /usr/share/nginx/html/

EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
```

### **docker-compose.yml** (Avvio completo stack)

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: libreria_postgres
    environment:
      POSTGRES_USER: libreria
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      POSTGRES_DB: libreria_db
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./postgres/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - app_network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U libreria"]
      interval: 10s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: libreria_backend
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DB_URL: jdbc:postgresql://postgres:5432/libreria_db
      DB_USER: libreria
      DB_PASSWORD: ${POSTGRES_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRES_IN: 15m
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - app_network
    ports:
      - "8080:8080"

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: libreria_frontend
    depends_on:
      - backend
    networks:
      - app_network
    ports:
      - "3000:8080"

networks:
  app_network:
    driver: bridge

volumes:
  postgres_data:
```

### **File .env (NON SCRIPTARE)**

```bash
# Configurazione applicazione e database
DB_URL=jdbc:postgresql://postgres:5432/libreria_db
DB_USER=libreria
DB_PASSWORD=${POSTGRES_PASSWORD}
SPRING_PROFILES_ACTIVE=docker

# Autenticazione JWT
JWT_SECRET=genera_una_cchiave_alfabetica_e_random_a_random_hex_length_32_plus
JWT_EXPIRES_IN=15m

# Porti di rete interna
PORT=8080

# Sicurezza
SECURITY_ENABLED=true
```

### **File .env.example (da versionare)**

```bash
# Configurazione applicazione e database
DB_URL=jdbc:postgresql://postgres:5432/libreria_db
DB_USER=libreria
DB_PASSWORD=<cambia_questa_cchiave>
SPRING_PROFILES_ACTIVE=docker

# Autenticazione JWT
JWT_SECRET=<cambia_questa_cchiave_hex_length_minimo_32_caratteri>
JWT_EXPIRES_IN=15m

# Porti di rete interna
PORT=8080

# Sicurezza
SECURITY_ENABLED=true
```

### **File .gitignore (da includere)**

```bash
.env                          # Configurazione locale secreta
backend/                    /target/*     # Build artifacts backend
frontend/src/              # File JS minificati
*.log                       # Log file di sistema
.dockerignore               # Esclusioni Docker specifiche
.DS_Store                  # Mac artefatti
.vscode/                    # VS Code settings
.idea/                      # IntelliJ settings
```

## 4. API Principali (Endpoint REST)

### **Autenticazione e Autorizzazione**

| Endpoint | Metodo | Ruolo | Descrizione |
|----------|--------|-------|-------------|
| `/api/auth/login` | POST | user, admin | Login utente che restituisce JWT access token e refresh token |
| `/api/auth/logout` | POST | user, admin | Logout che annulla il token e cancella lo refresh token |

### **Gestione Libri**

| Endpoint | Metodo | Ruolo | Operazioni disponibili |
|----------|--------|-------|----------------------|
| `/api/book` | GET | Admin, User | Legge tutti i libri (admin) o solo i propri libri (user) con permessi |
| `/api/book/{id}` | GET | Admin, User | Legge un libro esistente (admin: tutto; user: solo propri libri) |
| `/api/book` | POST | Admin, User | Aggiunge un nuovo libro (admin: completo; user: senza attributo editore/autore esterno) |
| `/api/book/{id}` | PUT | Admin, User | Aggiorna il libro (admin: completo; user: solo campi di proprietà) |
| `/api/book/{id}` | DELETE | Admin, User | Cella il libro (admin: con cascata; user: solo i propri libri) |

### **Gestione Autore**

| Endpoint | Metodo | Ruolo | Operazioni |
|----------|--------|-------|------------|
| `/api/author` | GET | Admin, User | Legge gli autori esistenti |
| `/api/author/{id}` | GET | Admin, User | Cerca autore per nome o id (admin: completo; user: solo propri libri) |
| `/api/author` | POST | Admin | Crea un nuovo autore (senza libro iniziale) |
| `/api/author/{id}` | PUT | Admin | Aggiorna l'autore esistente |
| `/api/author/{id}` | DELETE | Admin | Cancella autore con cascata su libri |

### **Gestione Case Editrici**

| Endpoint | Metodo | Ruolo | Operazioni |
|----------|--------|-------|------------|
| `/api/publisher` | GET | Admin, User | Legge le case editrici esistenti |
| `/api/publisher/{id}` | GET | Admin, User | Cerca editore per nome o id (admin: completo; user: solo propri libri) |
| `/api/publisher` | POST | Admin | Crea un nuovo editore |
| `/api/publisher/{id}` | PUT | Admin | Aggiorna l'editore esistente |
| `/api/publisher/{id}` | DELETE | Admin | Cancella editore con cascata su libri |

### **Gestione Posizione**

| Endpoint | Metodo | Ruolo | Operazioni |
|----------|--------|-------|------------|
| `/api/position` | GET | Admin, User | Legge le posizioni esistenti |
| `/api/position/{id}` | GET | Admin, User | Cerca posizione per nome o id (admin: completo; user: solo propri libri) |
| `/api/position` | POST | Admin | Crea un nuova posizione |
| `/api/position/{id}` | PUT | Admin | Aggiorna la posizione esistente |
| `/api/position/{id}` | DELETE | Admin | Cancella posizione con cascata su libri |

### **Gestione Utente**

| Endpoint | Metodo | Ruolo | Operazioni |
|----------|--------|-------|------------|
| `/api/user/me` | GET | Admin, User | Legge i dati utente e permessi di accesso |
| `/api/user/me` | PUT | Admin, User | Aggiorna nome utente e password (senza esporre password) |

### **Permessi Utente: Regole di Autorizzazione**

1. **Utente standard**: può modificare solo i libri con cui è correlato tramite `accesso_libri` e `domain_type='libri'`
2. **Autore esterno**: un utente non può leggere un autore esterno a meno che l'utente abbia accesso a quel libro o sia admin
3. **Editore esterno**: simile all'autore, il controllo di permessi passa attraverso i libri
4. **Posizione esterna**: il controllo di permessi è legato all'uso dei libri collegati

### **JWT Token e Autenticazione**

- **Access Token**: 15 minuti di vita (refresh token) per operazioni API
- **Refresh Token**: 7 giorni di vita per autenticazione persistente
- **Logout**: cancella sia il refresh token che annulla l'access token su server

## 5. Implementazione Graduale Consigliata

### **Fase 1: Backend Base (Autenticazione)**
- Spring Boot boilerplate
- Configurazione Security con JWT
- Entity e Repository base per Utente
- Controller Auth, Service Auth
- Endpoint login/logout

### **Fase 2: Gestione Libri**
- Entity e Repository di Book
- BookController e BookService
- Logica permessi utente (utente = admin o user)

### **Fase 3: Anagrafiche Esterne**
- Autore, Publisher, Position
- Relazioni many-to-many con libri
- Controllo permessi su lettura esterna

### **Fase 4: Documentazione e Testing**
- OpenAPI/Swagger documentation
- Test unitari con Spring Boot WebTest
- Validazione input su tutti gli endpoint

## 6. Requisiti di Sicurezza

### **Authenticazione JWT**
- `Access Token` (short-lived, ~15 minuti) per API REST
- `Refresh Token` (longer-lived, ~7 giorni) per autenticazione persistente
- `Refresh token revocation` su logout

### **Autorizzazione**
- **admin**: ruolo='admin' → può fare tutto su tutte le entità
- **user**: ruolo='user' → può modificare solo i propri libri, ma leggere qualsiasi entità esterna (autore, editore, posizione) senza averla registrata come propria

## 7. Risposte ai Comandi

### **Sviluppo Backend**

```bash
# Build backend con Maven
cd backend && mvn clean package

# Esecuzione del backend server
cd backend/target && java -jar spring-boot.jar

# Avvio server per debug
cd ../backend/src/main/java/com/libreria && mvn exec:java -Dexec.mainClass="com.libreria.config.SecurityConfig"
```

### **Sviluppo Frontend**

```bash
# Esecuzione frontend statico
cd frontend/ && \
  npm install && \
  npm start
```

### **Deploy Backend** (per app iOS)

```bash
cd backend/target && javap -classpath spring-boot.jar com.libreria.config.SecurityConfig
java -jar spring-boot.jar
```

### **Avvio Infrastructure Docker**

```bash
# Avvio completo stack con PostgreSQL, backend e frontend
docker compose up --build

# Controllare healthcheck di PostgreSQL
docker compose ps
docker compose logs postgres

# Ingressi a servizi
docker compose run --service backend sh -c "curl http://localhost:8080"
docker compose run --service frontend sh -c "open http://localhost:3000"
```

## 8. Schema di Implementazione Consigliato

### **Backend (Java Spring Boot)**
1. Creare `pom.xml` con dipendenze: Spring Boot Starter Web, Security, Data JPA, JWT
2. Impostare `application.properties` per database e JWT secret
3. Definire le Entity JPA
4. Creare Repository con `@Entity` e query personalizzate
5. Implementare AuthService (login/logout) con logica di permessi
6. Implementare BookService con controllo permissions (utente = admin o user)
7. Creare controller REST per tutte le entità
8. Aggiungi validazione input (@Valid, DTO)
9. Documentare API con OpenAPI

### **Frontend**
1. HTML index.html con routing client-side (SPA)
2. CSS responsive base con variabili e media queries
3. JS auth.js per gestione sessione JWT
4. UI moduli responsivi (book list, book details, forms)
5. Responsive design: mobile first, breakpoint >768px

### **Docker**
1. Backend Dockerfile multi-stage (build Maven → JRE leggero)
2. Frontend Dockerfile + nginx.conf
3. docker-compose.yml con healthcheck e dipendenze service_healthy
4. .env.example per variabili d'ambiente da versionare
5. .gitignore per escludere .env locale

## 9. Verifiche di Installazione

### **Backend**
```bash
# Prerequisiti
java --version      # Java 21+ (eclipse-temurin:21)
docker --version     # Docker runtime

# Build backend
cd backend && mvn clean package -DskipTests
```

### **Frontend**
```bash
# Prerequisiti
node --version       # Node.js LTS
npm --version        # npm 10+

# Build frontend
cd frontend/ && npm install
```

### **Containerizzazione**
```bash
# Avvio stack completo
docker compose up --build
```
