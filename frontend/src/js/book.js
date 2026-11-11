/* =====================================
   Gestione Libri: CRUD operations
   ===================================== */

// API REST per gestione libri
const BOOK_API = {
    // Lista tutti i libri (admin: tutti, user: solo propri)
    list: async function(options = {}) {
        let url = '/api/book';
        if (options.limit) {
            url += '?limit=' + options.limit;
        }
        return fetchWithAuth(url);
    },

    // Cerca libro per titolo o autore
    search: async function(query) {
        return fetchWithAuth('/api/book/search', {
            method: 'GET',
            body: JSON.stringify({ query })
        });
    },

    // Crea nuovo libro (senza attributo editore/autore esterno)
    create: async function(data) {
        // User può creare solo senza editore/autore esterno
        return fetchWithAuth('/api/book', {
            method: 'POST',
            body: JSON.stringify({
                ...data,
                autore_nominativo: '',
                editor_id: null
            })
        });
    },

    // Aggiorna libro (admin: completo; user: solo campi di proprietà)
    update: async function(id, data) {
        // User non può modificare editore/autore esterno
        if (data.autore_nominativo && data.autore_nominativo !== '') {
            console.error('Non puoi aggiungere autore esterno');
            return null;
        }
        return fetchWithAuth('/api/book/' + id, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    // Cella libro
    delete: async function(id) {
        // Admin: elimina libro e tutte le relazioni (cascata)
        return fetchWithAuth('/api/book/' + id, {
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

// Formatta monete
function formatMoney(amount) {
    return parseFloat(amount).toLocaleString('it-IT', { style: 'currency', currency: 'EUR' });
}

// Renderizza lista libri nella pagina
async function renderBookList() {
    const container = document.getElementById('book-list-container');
    if (!container) return;

    // Carica libri dall'API
    try {
        const res = await BOOK_API.list();
        const books = await res.json();

        let html = '<table><thead><tr>';
        html += '<th>Titolo e Edizione</th>';
        html += '<th>Autore</th>';
        html += '<th>Casa Editrice</th>';
        html += '<th>Posizione</th>';
        html += '<th>Copie Possedute</th>';
        html += '<th>Annotazioni</th>';
        html += '</tr></thead><tbody>';

        books.forEach(book => {
            html += '<tr>';
            html += '<td>' + book.titolo_edizione + '</td>';
            html += '<td>' + (book.autore_nominativo || 'N/A') + '</td>';
            html += '<td>' + (book.editore_id ? book.denominazione : 'N/A') + '</td>';
            html += '<td>' + (book.posizione_id ? book.nome_comune : 'N/A') + '</td>';
            html += '<td>' + book.copie_possedute + '</td>';
            html += '<td>' + (book.annotazioni || '(nessun annotazione)') + '</td>';
            html += '</tr>';
        });

        html += '</tbody></table>';
        container.innerHTML = html;
    } catch (e) {
        container.innerHTML = '<p>Errore: ' + e.message + '</p>';
    }
}
