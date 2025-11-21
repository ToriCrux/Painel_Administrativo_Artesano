// Armazena o token JWT no navegador
export function setAuthToken(token: string) {
  localStorage.setItem("token", token);
}

// Obtém o token armazenado
export function getAuthToken(): string | null {
  return localStorage.getItem("token");
}

// Remove o token (logout futuro)
export function clearAuthToken() {
  localStorage.removeItem("token");
}
