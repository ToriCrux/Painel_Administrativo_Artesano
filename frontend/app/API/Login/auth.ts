import Cookies from "js-cookie";

/**
 * 🔹 Armazena o token JWT em cookie
 * - expira em 1 dia
 * - usa HTTPS (secure: true)
 */
export function setAuthToken(token: string) {
  Cookies.set("token", token, { expires: 1, secure: true, sameSite: "Strict" });
}

/**
 * 🔹 Obtém o token armazenado no cookie
 */
export function getAuthToken(): string | undefined {
  return Cookies.get("token");
}

/**
 * 🔹 Remove o token (logout)
 */
export function clearAuthToken() {
  Cookies.remove("token");
}

/**
 * 🔹 Verifica se o token é válido (não expirou)
 * - decodifica o payload do JWT localmente
 */
export function isTokenValid(token: string | undefined): boolean {
  if (!token) return false;

  try {
    const [, payloadBase64] = token.split(".");
    const payload = JSON.parse(atob(payloadBase64));
    const exp = payload.exp * 1000; // exp é em segundos → converte p/ ms
    return Date.now() < exp;
  } catch {
    return false;
  }
}

/**
 * 🔹 Função de logout completo (limpa token + redireciona)
 */
export function logout() {
  clearAuthToken();
  if (typeof window !== "undefined") {
    window.location.href = "/Home";
  }
}
