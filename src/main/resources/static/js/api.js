// ✅ Automatically select correct backend depending on environment
const API_BASE = window.location.hostname.includes("herokuapp.com")
    ? "https://edusmart-22970a53efba.herokuapp.com/api"
    : "http://localhost:8080/api";

/**
 * Generic API fetch wrapper for JWT-based authentication.
 */
async function apiFetch(endpoint, options = {}) {
    const token = localStorage.getItem("token");
    const headers = {
        "Content-Type": "application/json",
        ...(token && options.auth !== false ? { "Authorization": `Bearer ${token}` } : {})
    };

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: options.method || "GET",
            headers: { ...headers, ...(options.headers || {}) },
            body: options.body ? JSON.stringify(options.body) : undefined
        });

        // Unauthorized → logout
        if (response.status === 401) {
            console.warn("Unauthorized → redirecting to login");
            logout();
            throw new Error("Unauthorized - token expired or invalid");
        }

        if (response.status === 204) return null; // No content

        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return await response.json();
        } else {
            return await response.text();
        }
    } catch (err) {
        console.error(`❌ API fetch error for ${endpoint}:`, err);
        throw err;
    }
}

/**
 * Login: store JWT + current user in localStorage
 */
async function login(email, password) {
    const res = await fetch(`${API_BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });

    if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Login failed. Check credentials.");
    }

    const data = await res.json();

    if (!data.accessToken) throw new Error("No token returned from server");

    localStorage.setItem("token", data.accessToken);
    localStorage.setItem("currentUser", JSON.stringify({
        id: data.id,
        email: data.email,
        role: data.role,
        firstName: data.firstName,
        lastName: data.lastName,
        verified: data.verified
    }));

    return getCurrentUser();
}

/**
 * Get current logged-in user from localStorage
 */
function getCurrentUser() {
    const user = localStorage.getItem("currentUser");
    return user ? JSON.parse(user) : null;
}

/**
 * Logout user
 */
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("currentUser");
    window.location.href = "login.html";
}
