import { getAuthToken } from "../Login/auth";

const API_BASE_URL = "http://localhost:8082/api/v1/estoque";

/* ============================
   🔹 Tipos (Models)
============================ */
export interface EstoqueItem {
  produtoId: number;
  produtoCodigo: string;
  produtoNome: string;
  saldo: number;
  versao: number;
  atualizadoEm: string;
}

export interface EstoqueResponse {
  content: EstoqueItem[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface MovimentoPayload {
  saldo: number;
}

/* ============================
   🔹 Buscar Estoque (GET)
============================ */
export const getEstoque = async (
  page = 0,
  size = 20,
  sort?: string[]
): Promise<EstoqueResponse> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const params = new URLSearchParams({
    page: String(page),
    size: String(size),
  });

  if (sort && sort.length > 0) {
    sort.forEach((s) => params.append("sort", s));
  }

  const response = await fetch(`${API_BASE_URL}?${params.toString()}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) throw new Error("Erro ao buscar estoque.");

  return await response.json();
};

/* ============================
   🔹 Ajustar Estoque (PUT)
   Substitui o saldo pelo valor informado
============================ */
export const ajustarEstoque = async (
  id: number,
  payload: MovimentoPayload
): Promise<EstoqueItem> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/${id}/movimentacoes/ajuste`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) throw new Error("Erro ao ajustar estoque.");

  return await response.json();
};

/* ============================
   🔹 Entrada de Estoque (POST)
   Soma o valor informado ao saldo atual
============================ */
export const entradaEstoque = async (
  id: number,
  payload: MovimentoPayload
): Promise<EstoqueItem> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/${id}/movimentacoes/entrada`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) throw new Error("Erro ao registrar entrada de estoque.");

  return await response.json();
};

/* ============================
   🔹 Saída de Estoque (POST)
   Subtrai o valor informado do saldo atual
============================ */
export const saidaEstoque = async (
  id: number,
  payload: MovimentoPayload
): Promise<EstoqueItem> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/${id}/movimentacoes/saida`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) throw new Error("Erro ao registrar saída de estoque.");

  return await response.json();
};

/* ============================
   🔹 Excluir Estoque (DELETE)
============================ */
export const deletarEstoque = async (id: number): Promise<void> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/${id}`, {
    method: "DELETE",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) throw new Error("Erro ao excluir estoque.");
};
