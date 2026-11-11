# Avvio Primo Start-Up - Libreria Personale

Documento di riferimento per l'initializzazione del progetto a "punto zero".

## 📋 Prerequisiti

Verifica che siano installati:

```bash
# Java JDK 21+ (eclipse-temurin distribuito su https://adoptium.net/)
java --version

# Node.js LTS (npm incluso)
node --version
npm --version

# Docker runtime
docker --version
docker compose version
```

## 🔧 Step-by-Step

### 1️⃣ Preparazione Backend (Maven)

**Obiettivo**: Costruire l'application jar del backend senza test

```bash
# cd al root del progetto
cd /Users/dbertini/work/progetti/testclaude

# Entrare nel directory backend
cd backend

# Build il JAR con Maven, saltando i test per accelerazione
mvn clean package -DskipTests

# Output attesa: backend/target/libreria-api-1.0.0-SNAPSHOT.jar
```

**Verifica**:

```bash
# Verifica che il JAR sia stato creato
ls -lh target/*.jar

# Verifica dimensioni (deve essere ~50-80MB per Spring Boot 21)
file target/libreria-api-1.0.0-SNAPSHOT.jar
```

---

### 2️⃣ Configurazione Docker (POSTGRES_PASSWORD e JWT_SECRET)

**Importante**: Creare prima il file `.env` con le variabili d'ambiente necessarie

```bash
# cd back al root
cd /Users/dbertini/work/progetti/testclaude

# Genera un JWT_SECRET random di almeno 32 caratteri
export RANDOM_JWT=$(openssl rand -hex 64)

# Copia .env.example per creare il file .env (versionabile!)
cp .env.example .env

# Aggiorna .env con i valori generati
sed -i '' 's/POSTGRES_PASSWORD=.*\/cambia_questa_cchiave/postgres_password='"'"'"$RANDOM_JWT"'/g' .env
sed -i '' 's/JWT_SECRET=.*\/cambia_questa_cchiave/jwt_secret='"'"'"$RANDOM_JWT"'/g' .env

# Verifica contenuti .env
cat .env
```

**Nota**: Il file `.env.example` è già presente e deve essere versionato (non segreto)

---

### 3️⃣ Preparazione Frontend

```bash
# cd al root
cd /Users/dbertini/work/progetti/testclaude

# Entrare nel directory frontend
cd frontend

# Installa le dipendenze NPM
npm install

# Output attesa: nod_modules/ popolato con tutte le dipendenze
```

**Verifica**:

```bash
# Verifica che npm install abbia completato correttamente
ls -la node_modules/ | head -20
```

---

### 4️⃣ Avvio Database PostgreSQL

**Importante**: Il primo avvio del DB crea automaticamente il database vuoto. Per evitare di perdere dati, NON usare `docker compose up` qui. Usare invece `docker compose up --build`.

```bash
# cd al root
cd /Users/dbertini/work/progetti/testclaude

# Avvia solo PostgreSQL in background (non ricostruisce il DB)
docker compose up postgres-db

# Output attesa: container "libreria_postgres" avviato e ready
```

**Verifica**:

```bash
# Controlla che PostgreSQL sia in running state
docker compose ps

# Verifica healthcheck
docker compose logs --tail=10 postgres-db | grep -i "ready"
```

---

### 5️⃣ Avvio Backend (Prima volta)

**Importante**: Il primo avvio backend eseguirà i script di init.sql se esistono nel DB. Se non esistono, il DB parte vuoto.

```bash
# cd al root
cd /Users/dbertini/work/progetti/testclaude

# Entrare nel directory backend
cd backend

# Avvia l'application Java del Spring Boot
java -jar target/libreria-api-1.0.0-SNAPSHOT.jar

# Output attesa: server avviato su localhost:8080 con message "Started Libreria API REST in 10.xxx seconds"
```

**Verifica**:

```bash
# Controlla che il server sia stato avviato correttamente
docker logs libreria_backend --tail=30 | grep -i "started"

# Test la health endpoint (da un altro terminale o con curl)
docker compose exec backend sh -c "curl -s http://localhost:8080/actuator/health"
```

**Nota**: Dopo questo step, NON riavviare il backend. Usare sempre `docker compose down` quando si vuole fermarlo, e `docker compose up` per riavviarlo.

---

### 6️⃣ Avvio Frontend (Prima volta)

```bash
# cd al root
cd /Users/dbertini/work/progetti/testclaude

# Entrare nel directory frontend
cd frontend

# Avvia il server NPM del frontend statico
npm start

# Output attesa: "npm start" message e browser che si apre su http://localhost:3000
```

**Verifica**:

```bash
# Verifica che il server sia in running state
lsof -i :3000 | grep LISTEN
```

**Nota**: Il frontend serve file statici con Nginx (come specificato nel PIANO.md) o serve direttamente via Node.js. Verifica nel Dockerfile frontend quale approccio viene usato.

---

### 7️⃣ Documentazione e Validazione

Dopo tutti i step:

```bash
# cd al root
cd /Users/dbertini/work/progetti/testclaude

# Verifica lo stack completo
docker compose ps

# Controlla logs backend
docker compose logs --tail=50 backend

# Controlla logs frontend
docker compose logs --tail=20 frontend

# Test l'API REST (devono essere disponibili i controller)
docker compose exec backend sh -c "curl -s http://localhost:8080/actuator"
```

**Checklist Finale**:

- [x] Backend build completato (`mvn clean package`)
- [x] Database PostgreSQL avviato in background
- [x] Frontend NPM install completato
- [x] Server backend avviato e responsive
- [x] Server frontend avviato e servente file statici
- [x] `.env` creato con secrets configurate
- [x] Docker compose network attivo

---

## ⚠️ Avvertenze Importanti

### NON RICOstruire il DB all'initialization!

Il primo avvio del DB (con `docker compose up`) ricostruisce tutto da zero. Se esiste già un DB popolato, usa sempre:

```bash
# Ferma tutto il stack
docker compose down

# Avvia solo PostgreSQL in background
docker compose up postgres-db

# Avvia backend senza ricostruzione DB
docker compose up backend

# (Frontend serve file statici, non ha bisogno di ricostruire)
```

### NON RICOstruire il BACKEND all'initialization!

La prima volta che si avvia il backend, deve essere sicuro di avere `init.sql` vuoto o commentato. Se l'avvierete con un init.sql non-vuoto, i dati iniziali verranno persi. Quindi:

1. Se è la prima volta che si crea il DB → usa `docker compose up --build` (ricostruisce da zero)
2. Se esiste già il DB → usate sempre `docker compose down && docker compose up backend` (NON ricostruisce nulla)

### NON RICOstruire il FRONTEND all'initialization!

Il frontend serve file statici, quindi non ha bisogno di build. Usa solo:

```bash
# Avvia frontend senza build
docker compose up frontend
```

## 🚀 Comandi Rapidi per Primo Setup

Copia e incolla nel terminale:

```bash
#!/bin/bash

# Primo setup completo Libreria Personale (NON ricostruisce nulla!)

set -e

echo "=== Primo Setup Libreria Personale ==="
cd /Users/dbertini/work/progetti/testclaude

# 1. Build backend (senza test)
echo "[1/4] Building backend..."
cd backend
mvn clean package -DskipTests || exit 1
cd ..

# 2. Verifica Docker compose
echo "[2/4] Checking Docker compose..."
docker compose ps > /dev/null 2>&1 || { echo "Docker compose network missing, start it..."; docker compose up --build > /dev/null 2>&1 & }

# 3. Avvia PostgreSQL in background (NON ricostruisce)
echo "[3/4] Starting PostgreSQL..."
docker compose up postgres-db || exit 1

# 4. Avvia Backend (NON ricostruisce)
echo "[4/4] Starting Backend..."
docker compose up backend || exit 1

echo "=== Primo Setup Completato! ==="
echo ""
echo "Il tuo stack ora è attivo:"
docker compose ps --format table --columns NAME,STATUS
```
