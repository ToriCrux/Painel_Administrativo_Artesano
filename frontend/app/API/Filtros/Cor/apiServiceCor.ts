import { getAuthToken } from "../../Login/auth";

const API_BASE_URL = "http://localhost:8082/api/v1";

export interface Cor {
  id: number;
  nome: string;
  hex: string;
  ativo: boolean;
  criadoEm: string;
  atualizadoEm?: string | null;
}

export interface CorPayload {
  nome: string;
  hex: string;
  ativo: boolean;
}

/**
 * 🔹 Cria uma nova cor
 */
export const criarCor = async (payload: CorPayload) => {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Token de autenticação não encontrado.");

    const response = await fetch(`${API_BASE_URL}/cores`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Erro ao criar cor.");
    }

    return await response.json();
  } catch (error) {
    console.error("Erro ao criar cor:", error);
    throw error;
  }
};

/**
 * 🔹 Busca todas as cores cadastradas
 */
export const getCores = async (): Promise<Cor[]> => {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Usuário não autenticado.");

    const response = await fetch(`${API_BASE_URL}/cores`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) throw new Error("Falha ao carregar cores.");

    const data = await response.json();
    return data.content || [];
  } catch (error) {
    console.error("Erro ao buscar cores:", error);
    throw error;
  }
};

/**
 * 🔹 Exclui uma cor pelo ID
 */
export const deleteCor = async (id: number): Promise<void> => {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Usuário não autenticado.");

    const response = await fetch(`${API_BASE_URL}/cores/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) throw new Error("Falha ao excluir cor.");
  } catch (error) {
    console.error("Erro ao excluir cor:", error);
    throw error;
  }
};

// 🔹 Atualizar cor (PUT)
export const updateCor = async (id: number, payload: CorPayload) => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/cores/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Erro ao atualizar cor.");
  }

  return await response.json();
};
