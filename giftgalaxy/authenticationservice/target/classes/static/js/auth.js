// Funzione per fetch con token JWT incluso
function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('jwtToken');
    
    // Se il token esiste, aggiungilo all'Authorization header
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': 'Bearer ' + token
        };
    }
    
    return fetch(url, options);
}