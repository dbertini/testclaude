# La mia libreria

## Scopo
Sito web responsive per gestire la mia libreria personale.
Anagrafiche: autore, casa editrice, posizione. Entità principale: libro.
Due livelli di accesso con login:
- amministratore: vede e gestisce tutto
- utente standard: vede e gestisce solo i propri libri
Il backend espone API REST, così in futuro potrà essere usato anche da un'app iOS.

## Stack
- Backend: Java 21, Spring Boot, Spring Security (JWT), Maven
- Database: PostgreSQL
- Frontend: HTML, CSS, JavaScript (responsive)

## Struttura
- backend/  → API REST in Java (Maven, Spring Boot)
- frontend/ → interfaccia web (HTML, CSS, JavaScript responsive)
- docker/   → containerizzazione (Docker, Docker Compose)
- docs/     → documentazione e schema del database

Per il piano completo di implementazione, struttura dati, API REST e configurazione Docker: consulta [docs/PIANO.md](docs/PIANO.md).

## Comandi
(da completare quando il progetto è creato)

### Sviluppo Backend (Java/Maven)
```bash
# Build backend con Maven
cd backend && mvn clean package -DskipTests

# Esecuzione del backend server
cd backend/target && java -jar spring-boot.jar
```

### Sviluppo Frontend
```bash
# Installa e avvia frontend statico
cd frontend/ && npm install && npm start
```

### Deploy Docker (Infrastruttura completa)
```bash
# Avvio completo stack con PostgreSQL, backend e frontend
docker compose up --build
```

## Convenzioni
- Tutti gli endpoint devono validare l'input
- I controlli sui permessi si fanno nel backend, mai solo nel frontend
- Le API non devono mai esporre le password
- Messaggi, commenti e documentazione in italiano