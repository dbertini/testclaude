/* =====================================
   Gestione Autori: CRUD operations
   ===================================== */

// API REST per gestione autori
const AUTHOR_API = {
    // Lista tutti gli autori (admin: completo; user: solo propri libri)
    list: async function(options = {}) {
        return fetchWithAuth('/api/author');
    },

    // Cerca autore per nome o id (admin: completo; user: solo propri libri)
    search: async function(query) {
        return fetchWithAuth('/api/author/search', {
            method: 'GET',
            body: JSON.stringify({ query })
        });
    },

    // Crea nuovo autore (solo admin, senza libro iniziale)
    create: async function(data) {
        return fetchWithAuth('/api/author', {
            method: 'POST',
            body: JSON.stringify({ ...data, libri_ids: [] })
        });
    },

    // Aggiorna autore esistente
    update: async function(id, data) {
        return fetchWithAuth('/api/author/' + id, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    // Cella autore con cascata su libri (solo admin)
    delete: async function(id) {
        return fetchWithAuth('/api/author/' + id, {
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

// Renderizza lista autori nella pagina
async function renderAuthorList() {
    const container = document.getElementById('author-list-container');
    if (!container) return;

    try {
        const res = await AUTHOR_API.list();
        const authors = await res.json();

        let html = '<table><thead><tr>';
        html += '<th>Nome Completo</th>';
        html += '<th>Nascita</th>';
        html += '<th>Morte (opzionale)</th>';
        html += '</tr></thead><tbody>';

        authors.forEach(author => {
            const death = author.morte ? '(†' + formatDate(author.morte) + ')' : '';
            html += '<tr><td>' + author.nome_completo + '</td>' +
                '<td>' + (author.nascita || 'N/A') + '</td>' +
                '<td>' + death + '</td></tr>';
        });

        html += '</tbody></table>';
        container.innerHTML = html;
    } catch (e) {
        container.innerHTML = '<p>Errore: ' + e.message + '</p>';
    }
}

// Aggiorna libro con autore esistente
async function linkAuthorToBook(bookId, authorId) {
    try {
        await BOOK_API.update(bookId, { autore_nominativo: authorId });
        renderBookList();
        showSuccess('Autore collegato');
    } catch (e) {
        showError('Errore: ' + e.message);
    }
}

// Formatta data di nascita/morte per display
function formatNameWithDates(name, birth, death) {
    return name + (birth ? ' (' + formatDate(birth) + ')' : '') + (death ? death : '');
}
