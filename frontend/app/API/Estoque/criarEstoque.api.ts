import { getAuthToken } from "../Login/auth";

const API_BASE_URL = "http://localhost:8082/api/v1";

/**
 * 🔹 Buscar produtos disponíveis (para dropdown)
 */
export const getProdutos = async () => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/produtos`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) throw new Error("Erro ao buscar produtos.");
  return await response.json();
};

/**
 * 🔹 Criar estoque zerado para um produto específico
 */
export const criarEstoqueZerado = async (produtoId: number) => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/estoque/criar/${produtoId}`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    const erro = await response.text();
    throw new Error(`Erro ao criar estoque: ${erro}`);
  }

  return await response.json();
};
