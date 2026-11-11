/* =====================================
   Autenticaizione: Gestione sessione JWT
   ===================================== */

// Controlla se utente è loggato
function isAuthenticated() {
    const token = localStorage.getItem('auth_token');
    return !!token;
}

// Salva token di autenticazione
function saveAuthToken(token) {
    localStorage.setItem('auth_token', token);
}

// Cancella token
function clearAuthToken() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('refresh_token');
}

// Fetch con autenticazione JWT
async function fetchWithAuth(url, options = {}) {
    const headers = { ...options.headers, 'Authorization': `Bearer ${localStorage.getItem('auth_token')}` };
    return fetch(url, { ...options, headers });
}

// Login utente
function login(username, password) {
    return fetchWithAuth('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password })
    }).then(res => res.json());
}

// Logout
function logout() {
    return fetchWithAuth('/api/auth/logout', { method: 'POST' });
}

// Avvio pagina: verifica autenticazione
document.addEventListener('DOMContentLoaded', async function() {
    if (!isAuthenticated()) {
        // Mostra modale login per utente non autorizzato
        const modal = document.getElementById('auth-modal');
        if (modal) {
            modal.style.display = 'flex';
        }
    } else {
        // Carica dati utente e mostra contenuti accessibili
        await loadUser();
        renderSections();
    }
});

// Carica dati utente corrente
async function loadUser() {
    try {
        const res = await fetchWithAuth('/api/user/me');
        if (res.status === 200) {
            localStorage.setItem('user_data', JSON.stringify(res.data));
        }
    } catch (e) {
        clearAuthToken();
    }
}

// Renderizza sezioni disponibili per l'utente
function renderSections() {
    const container = document.getElementById('sections-container');
    if (!container) return;

    // Se utente è loggato, mostra opzioni di accesso
    if (isAuthenticated()) {
        container.innerHTML += `
            <div class="section-item">
                <span>🔐 Amministratore</span>
                <button onclick="location.href='/js/auth.js#admin'">Accedi</button>
            </div>`;
    }
}

// Close modal auth
function closeAuthModal() {
    document.getElementById('auth-modal').style.display = 'none';
    document.getElementById('error-message').textContent = '';
}

// Mostra errore
function showErrorMessage(msg) {
    const errorDiv = document.getElementById('error-message');
    if (errorDiv) {
        errorDiv.textContent = msg;
    }
}
