/* =====================================
   Gestione Posizioni: CRUD operations
   ===================================== */

// API REST per gestione posizioni
const POSITION_API = {
    // Lista tutte le posizioni (admin: completo; user: solo propri libri)
    list: async function(options = {}) {
        return fetchWithAuth('/api/position');
    },

    // Cerca posizione per nome o id (admin: completo; user: solo propri libri)
    search: async function(query) {
        return fetchWithAuth('/api/position/search', {
            method: 'GET',
            body: JSON.stringify({ query })
        });
    },

    // Crea nuova posizione (solo admin, senza libro iniziale)
    create: async function(data) {
        return fetchWithAuth('/api/position', {
            method: 'POST',
            body: JSON.stringify({ ...data, libri_ids: [] })
        });
    },

    // Aggiorna posizione esistente
    update: async function(id, data) {
        return fetchWithAuth('/api/position/' + id, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    // Cella posizione con cascata su libri (solo admin)
    delete: async function(id) {
        return fetchWithAuth('/api/position/' + id, {
            method: 'DELETE'
        });
    }
};

// Formati timestamp per display
function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    const d = new Date(dateStr);
    return d.toLocaleDateString('it-IT');
}

// Renderizza lista posizioni nella pagina
async function renderPositionList() {
    const container = document.getElementById('position-list-container');
    if (!container) return;

    try {
        const res = await POSITION_API.list();
        const positions = await res.json();

        let html = '<table><thead><tr>';
        html += '<th>Nome Comune</th>';
        html += '<th>Paese</th>';
        html += '<th>Codice PA</th>';
        html += '<th>Natura</th>';
        html += '</tr></thead><tbody>';

        positions.forEach(position => {
            const country = position.paese ? '🇦🇺 ' + (position.paese || 'N/A') : '(N/A)';
            html += '<tr><td>' + position.nome_comune + '</td>' +
                '<td>' + country + '</td>' +
                '<td>' + (position.codice_pa || 'N/A') + '</td>' +
                '<td>' + position.natura + '</td></tr>';
        });

        html += '</tbody></table>';
        container.innerHTML = html;
    } catch (e) {
        container.innerHTML = '<p>Errore: ' + e.message + '</p>';
    }
}

// Collega libro a posizione esistente
async function linkPositionToBook(bookId, positionId) {
    try {
        await BOOK_API.update(bookId, { posizione_id: positionId });
        renderBookList();
        showSuccess('Posizione collegata');
    } catch (e) {
        showError('Errore: ' + e.message);
    }
}

// Mostra selezione posizione come popup
function selectPosition(position) {
    alert('Posizione selezionata:\n' + JSON.stringify(position, null, 2));
}
