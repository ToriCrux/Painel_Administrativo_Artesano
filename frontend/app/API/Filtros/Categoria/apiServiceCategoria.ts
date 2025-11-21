import { getAuthToken } from "../../Login/auth";

const API_BASE_URL = "http://localhost:8082/api/v1/categorias";

export interface Categoria {
  id: number;
  nome: string;
  ativo: boolean;
  criadoEm: string;
}

export interface CategoriaPayload {
  nome: string;
  ativo: boolean;
}

// 🔹 Criar categoria (POST)
export const criarCategoria = async (payload: CategoriaPayload) => {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Token de autenticação não encontrado.");

    const response = await fetch(`${API_BASE_URL}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Erro ao criar categoria.");
    }

    return await response.json();
  } catch (error) {
    console.error("Erro ao criar categoria:", error);
    throw error;
  }
};

// 🔹 Buscar todas as categorias (GET)
export const getCategorias = async (): Promise<Categoria[]> => {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Usuário não autenticado.");

    const response = await fetch(API_BASE_URL, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Erro ao buscar categorias.");
    }

    const data = await response.json();
    return data.content || [];
  } catch (error) {
    console.error("Erro ao buscar categorias:", error);
    throw error;
  }
};

// 🔹 Excluir categoria por ID (DELETE)
export const deleteCategoria = async (id: number): Promise<void> => {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Usuário não autenticado.");

    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Erro ao excluir categoria.");
    }
  } catch (error) {
    console.error("Erro ao excluir categoria:", error);
    throw error;
  }
};

// 🔹 Atualizar categoria (PUT)
export const updateCategoria = async (
  id: number,
  payload: CategoriaPayload
): Promise<Categoria> => {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Usuário não autenticado.");

    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Erro ao atualizar categoria.");
    }

    return await response.json();
  } catch (error) {
    console.error("Erro ao atualizar categoria:", error);
    throw error;
  }
};
