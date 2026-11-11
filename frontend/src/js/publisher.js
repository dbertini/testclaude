/* =====================================
   Gestione Case Editrici: CRUD operations
   ===================================== */

// API REST per gestione case editrici
const PUBLISHER_API = {
    // Lista tutte le case editrici (admin: completo; user: solo propri libri)
    list: async function(options = {}) {
        return fetchWithAuth('/api/publisher');
    },

    // Cerca editore per nome o id (admin: completo; user: solo propri libri)
    search: async function(query) {
        return fetchWithAuth('/api/publisher/search', {
            method: 'GET',
            body: JSON.stringify({ query })
        });
    },

    // Crea nuovo editore (solo admin, senza libro iniziale)
    create: async function(data) {
        return fetchWithAuth('/api/publisher', {
            method: 'POST',
            body: JSON.stringify({ ...data, libri_ids: [] })
        });
    },

    // Aggiorna editore esistente
    update: async function(id, data) {
        return fetchWithAuth('/api/publisher/' + id, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    // Cella editore con cascata su libri (solo admin)
    delete: async function(id) {
        return fetchWithAuth('/api/publisher/' + id, {
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

// Renderizza lista case editrici nella pagina
async function renderPublisherList() {
    const container = document.getElementById('publisher-list-container');
    if (!container) return;

    try {
        const res = await PUBLISHER_API.list();
        const publishers = await res.json();

        let html = '<table><thead><tr>';
        html += '<th>Denominazione</th>';
        html += '<th>Sede (Città/PA)</th>';
        html += '<th>Anno Fondazione</th>';
        html += '</tr></thead><tbody>';

        publishers.forEach(publisher => {
            const year = publisher.founded_year ? '1' + publisher.founded_year : 'N/A';
            html += '<tr><td>' + publisher.denominazione + '</td>' +
                '<td>' + (publisher.sede_citta || 'N/A') + '(' + (publisher.p_a || 'N/A') + ')' +
                '<td>' + year + '</td></tr>';
        });

        html += '</tbody></table>';
        container.innerHTML = html;
    } catch (e) {
        container.innerHTML = '<p>Errore: ' + e.message + '</p>';
    }
}

// Collega libro a editore esistente
async function linkPublisherToBook(bookId, publisherId) {
    try {
        await BOOK_API.update(bookId, { editor_id: publisherId });
        renderBookList();
        showSuccess('Editore collegato');
    } catch (e) {
        showError('Errore: ' + e.message);
    }
}
