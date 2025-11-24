import { getAuthToken } from "../Login/auth";

const API_BASE_URL = "http://localhost:8082/api/v1/produtos";

/* ============================
   🔹 Tipos (Models)
============================ */
export interface Cor {
  id: number;
  nome: string;
  hex: string;
}

export interface Categoria {
  id: number;
  nome: string;
}

export interface Produto {
  id: number;
  codigo: string;
  nome: string;
  categoria: Categoria;
  categoriaNome?: string; 
  medidas: string;
  precoUnitario: number;
  corIds?: number[];
  cores?: Cor[];
  ativo: boolean;
  descricao: string;
}


export interface ProdutoPayload {
  codigo: string;
  nome: string;
  categoriaNome: string;
  corIds: number[];
  medidas: string;
  precoUnitario: number;
  ativo: boolean;
  descricao: string;
}

/* ============================
   🔹 Criar Produto (POST)
============================ */
export const criarProduto = async (payload: ProdutoPayload) => {
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
    throw new Error(errorText || "Erro ao cadastrar produto.");
  }

  return await response.json();
};

/* ============================
   🔹 Buscar todos os produtos (GET)
============================ */
export const getProdutos = async (): Promise<Produto[]> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(API_BASE_URL, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) throw new Error("Erro ao buscar produtos.");

  const data = await response.json();
  return data.content || [];
};

/* ============================
   🔹 Buscar produto por ID (GET /{id})
============================ */
export const getProdutoById = async (id: number): Promise<Produto> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/${id}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) throw new Error("Erro ao buscar produto.");

  return await response.json();
};

/* ============================
   🔹 Atualizar Produto (PUT /{id})
============================ */
export const updateProduto = async (id: number, payload: ProdutoPayload) => {
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
    throw new Error(errorText || "Erro ao atualizar produto.");
  }

  return await response.json();
};

/* ============================
   🔹 Excluir Produto (DELETE /{id})
============================ */
export const deleteProduto = async (id: number): Promise<void> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/${id}`, {
    method: "DELETE",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) throw new Error("Erro ao excluir produto.");
};
