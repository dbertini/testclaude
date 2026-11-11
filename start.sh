#!/bin/bash

# ==========================================
# LIBRERIA PERSONALE - Avvio Automatico
# ==========================================
# Questo script serve AVVIA IL SERVER SENZA RICOstruire il DB
# Il container del DB sarà già in corsa e i dati saranno preservati
#
# Usage: sucrash start.sh
# Prerequisiti: Docker runtime attivo, DB già avviato (con docker compose up)
# ==========================================

set -e

PROJECT_ROOT="/Users/dbertini/work/progetti/testclaude"
SCRIPT_PATH="${PROJECT_ROOT}/start.sh"

echo "=== Libreria Personale - Avvio Automatico ===" 2>&1 >> /dev/null
echo "" 2>&1 >> /dev/null

# Verifica Docker Compose è attivo
if ! docker compose version &>/dev/null; then
    echo "❌ ERROR: Docker compose non è installato!" 2>&1 >> /dev/null
    echo "Install Docker Runtime:" 2>&1 >> /dev/null
    echo "  brew install --cask docker" 2>&1 >> /dev/null
    exit 1
fi

# Verifica che il DB sia già in corsa (NON ricostruire)
if ! docker compose ps > /dev/null 2>&1; then
    # Se il network non esiste, è la prima volta: usa --build
    echo "⚠️  Database e stack non sono avviati. Avvio iniziale..." 2>&1 >> /dev/null
    docker compose up --build 2>&1
    sleep 30
else
    # Network esiste ma i container non stanno correndo: fermare e riavviare
    echo "⚠️  Stack esistente in esecuzione. Avvio senza rebuild..." 2>&1 >> /dev/null
fi

# Ferma tutto il stack completo (preserva i volumes)
docker compose down --remove-orphans --volumes=false --force-recreate > /dev/null 2>&1 &

# Avvia PostgreSQL (senza build, usiamo --no-recreate ma è ok perché non abbiamo mai avviato)
docker compose up -d postgres-db --no-recreate > /dev/null 2>&1 &

sleep 5  # Aspetta che il DB parta

# Avvia Backend (senza rebuild, usiamo --no-recreate)
docker compose up -d backend --no-recreate > /dev/null 2>&1 &

# Verifica che tutto stia correndo
echo "" 2>&1 >> /dev/null
echo "=== Stato Containeri ===" 2>&1 >> /dev/null
docker compose ps 2>&1 | grep -v "upcoming" | grep -v "pending" || true 2>&1 >> /dev/null

echo "" 2>&1 >> /dev/null
echo "=== Avvio Completato! ===" 2>&1 >> /dev/null
echo "Il tuo server ora sta correndo:" 2>&1 >> /dev/null
echo "  - Frontend: http://localhost:3000" 2>&1 >> /dev/null
echo "  - Backend: localhost:8080 (API REST)" 2>&1 >> /dev/null
echo "" 2>&1 >> /dev/null
echo "Per fermare tutto il sistema:" 2>&1 >> /dev/null
echo "  docker compose down" 2>&1 >> /dev/null

# Exit silent
exit 0 2>&1
