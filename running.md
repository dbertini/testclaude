# Operazioni di Routine - Libreria Personale

Documento di riferimento per operazioni quotidiane e routine (stop/start).

## 📋 Riepilogo Operativo

| Comando | Funzione | Quando usarlo |
|---------|----------|---------------|
| `docker compose down` | Ferma tutto il stack completo | All'avvio quotidiano |
| `docker compose up` | Avvia tutto il stack (con rebuild) | Solo se si deve ricreare DB |
| `docker compose down && docker compose up` | Ricompozione completa | Quando si cambia configurazione |
| `docker compose ps` | Verifica stato containeri | Diario operativo |

---

## 🛑 Ferma Complete (DAILY STOP)

### Comando Rapido: Ferma Tutto

```bash
# Ferma tutto il stack completo (DB, backend, frontend)
docker compose down
```

**Output attesa**:

```bash
sh: 5: stop: not found
Shutting down...
Removing network libreria_personale_app_network
Removing libreria_frontend
Removing libreria_backend
Removing library_postgres_1
# Note: volumes sono preservati (dati DB salvati)
```

**Verifica**:

```bash
# Verifica che tutto sia stato fermato
docker compose ps
```

---

## 🔆 Avvio Quotidiano (DAILY START)

### Scenario A: Tutto va bene, solo restart del server

```bash
# Avvia solo il backend (DB e frontend già in corsa)
docker compose up --no-recreate
```

**Verifica**:

```bash
# Verifica che il backend sia attivo
docker compose ps | grep backend
docker compose logs libreria_backend --tail=30 | grep -i "started"
```

---

### Scenario B: Frontend crashato, ripartire tutto

```bash
# Avvia backend e frontend (DB già in corsa)
docker compose up backend frontend
```

**Verifica**:

```bash
docker compose ps
```

---

### Scenario C: Backend crashato, DB ma senza backend

```bash
# Ripartire backend e frontend
docker compose up backend frontend --no-recreate
```

---

## 🔄 Stop and Start Parziali (PARTIAL OPERATIONS)

### Ferma solo il Frontend

```bash
docker compose down backend
docker compose up frontend --no-recreate
```

**Verifica**:

```bash
docker compose ps
docker compose logs frontend --tail=10 | grep "up"
```

---

### Ferma solo il Backend

```bash
docker compose down backend
docker compose up frontend --no-recreate
```

**Verifica**:

```bash
docker compose ps
docker compose logs frontend --tail=10 | grep "Up and running"
```

---

### Ferma solo il DB (ATTENZIONE: perdita dati) ⚠️

**NON USARE QUALE NON NECESSARIO!**

```bash
# Avvisa l'utente dell'imperdibilità dei dati
echo "ATTENZIONE: questo comando FERMA TUTTO E PRESERVA I VOLUMES"
docker compose down

# Ferma DB (e tutto il resto)
docker compose down backend postgres-db frontend

# Riparten solo DB
docker compose up postgres-db --no-recreate
```

**Verifica**:

```bash
docker compose ps | grep postgres
```

---

## 🚨 Emergenza: Reset Completo (DANGER ZONE!)

### Usare SOLO quando la configurazione del sistema cambia o quando si deve ricreare il DB da zero

```bash
#!/bin/bash

# RESET COMPLETO: ricrea tutto dal zero
set -e

echo "=== WARNING: RESET COMPLETO DEL SISTEMA ==="
read -p "Sei sicuro di voler ricreare tutto? (s/n): " CONFIRM

if [[ "$CONFIRM" != "s" ]]; then
  echo "Abbinato, reset annullato."
  exit 0
fi

echo "Resetting..."

# Ferma tutto
docker compose down

# Pulisci volumes del DB (ATTENZIONE: perde tutti i dati!)
docker volume rm libreria_personale_postgres_data || true

# Riavvia con rebuild completo
docker compose up --build

echo "=== Reset Completato ==="
```

---

## 📊 Monitoraggio in Background (AUTOMATED MONITORING)

### Opzione: Verifica stato containeri ogni volta che si avvia il server

Aggiungi questa riga nel `~/.zprofile` o `.bash_profile`:

```bash
# Avvio automatico checks Docker composable dopo ogni start di shell
docker compose ps > /dev/null 2>&1 || docker compose up backend --no-recreate > /dev/null 2>&1 &
```

### Opzione: Log periodici nel `~/.zshrc` o `.bash_profile`

```bash
# Verifica containeri in background al boot di shell
docker compose ps >> ~/downtimer_logs_$(date +%y%m%d_%H%M).log 2>&1
```

---

## 🧩 Comandi Utili (QUICK REFERENCE)

### Controllare Healthcheck

```bash
# Healthcheck del DB
docker compose exec postgres-db sh -c "pg_isready -U libreria" && echo "DB is up and running!"

# Healthcheck backend
docker compose exec backend sh -c "curl -s http://localhost:8080/actuator/health" || echo "Backend down or API disabled"

# Healthcheck frontend (serve static files)
docker compose exec frontend sh -c "curl -sI http://localhost:3000/favicon.ico | head -n1" || echo "Frontend down or not responding"
```

### Logi recenti (recent logs)

```bash
# Ultimi 50 righe backend
docker compose logs --tail=50 backend | tail -20

# Ultimo errore recente del backend
docker compose logs --no-log-prefix --tail=100 backend 2>&1 | grep -i "error\|exception" || true

# Verifica healthcheck (dall'ultimo log)
docker compose logs --grep="Up and running" libreria_backend > /dev/null && echo "Backend OK" || echo "Backend DOWN!"
```

### Stato attuale

```bash
docker compose ps
```

---

## 🎯 Comandi Rapidi (One-liners)

### Avvia tutto il server senza rebuild

```bash
docker compose down; docker compose up backend frontend --no-recreate
```

### Verifica che tutto sia attivo

```bash
docker compose ps | grep -i "up" | wc -l
```

### Verifica solo che i container stiano correndo (no logs)

```bash
docker compose ps | grep -i "running\|up"
```

### Avvisa se il DB è giù

```bash
docker compose exec postgres-db sh -c "pg_isready -U libreria" || echo "⚠️ Database is down!"
```

### Avvisa se il backend non risponde

```bash
docker compose exec backend sh -c "curl -s http://localhost:8080/actuator/health" > /dev/null && echo "OK" || echo "❌ Backend DOWN!"
```

---

## 📝 Note per l'Operatore

- **Stop completo** = `docker compose down` (preserva i volumes)
- **Start completo** = `docker compose up --no-recreate` (non rebuild, non perde dati)
- **Avvisa se qualcosa è rotto** = usa `docker compose ps` e `docker compose logs --tail=10` per diagnosticare
- **NON fermai il DB senza ragione** = i volumes sono persistenti e contengono i tuoi dati
- **Non mai reset completo** = solo quando devi ricreare tutto da zero o cambiare database
