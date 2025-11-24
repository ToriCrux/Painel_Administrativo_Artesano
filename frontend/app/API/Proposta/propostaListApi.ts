import { getAuthToken } from "@/app/API/Login/auth";

const API_BASE_URL = "http://localhost:8082/api/v1/propostas";

export interface Cliente {
  nome: string;
  cpfCnpj: string;
  uf: string;
}

export interface Produto {
  nomeProduto: string;
  quantidade: number;
  precoUnitario: number;
}

export interface Proposta {
  id: number;
  codigo: string;
  nomeVendedor: string;
  dataProposta: string;
  dataValidade: string;
  total: number;
  cliente: Cliente;
  produtos: Produto[];
}

export interface PropostaResponse {
  id: number;
  codigo: string;
  nomeVendedor: string;
  dataProposta: string;
  dataValidade: string;
  total: number;
  cliente: {
    nome: string;
    cpfCnpj: string;
    telefone: string;
    email: string;
    cep: string;
    endereco: string;
    bairro: string;
    cidade: string;
    uf: string;
    referencia: string;
    complemento: string;
  };
  produtos: {
    codigoProduto: string;
    nomeProduto: string;
    quantidade: number;
    precoUnitario: number;
  }[];
}

/**
 * 🔹 Buscar todas as propostas orçamentárias (com paginação)
 */
export const getPropostas = async (page = 0, size = 100): Promise<Proposta[]> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}?page=${page}&size=${size}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) throw new Error("Erro ao buscar propostas.");

  const data = await response.json();
  return data.content || [];
};

/**
 * 🔹 Buscar uma proposta específica pelo ID
 */
export const getPropostaById = async (id: number): Promise<Proposta> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/${id}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) throw new Error("Erro ao buscar proposta por ID.");

  return await response.json();
};


export const getPropostaByCodigo = async (codigo: string): Promise<PropostaResponse> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(`${API_BASE_URL}/codigo/${codigo}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    const msg = await response.text();
    throw new Error(`Erro ao buscar proposta por código: ${msg}`);
  }

  return await response.json();
};
