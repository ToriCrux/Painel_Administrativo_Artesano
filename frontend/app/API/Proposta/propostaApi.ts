import { Proposta } from "@/app/Sistema/Proposta/Criar/hooks/useProposta";
import { getAuthToken } from "../Login/auth";


const API_BASE_URL = "http://localhost:8082/api/v1";

/**
 * 🔹 Cria uma nova proposta orçamentária
 */
export const criarProposta = async (proposta: Proposta) => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const propostaBody = {
    ...proposta,
    dataProposta: proposta.dataProposta || new Date().toISOString().split("T")[0],
    dataValidade: proposta.dataValidade || new Date().toISOString().split("T")[0],
    cliente: { ...proposta.cliente },
    produtos: proposta.produtos.map((p) => ({
      codigoProduto: p.codigoProduto,
      nomeProduto: p.nomeProduto,
      quantidade: Number(p.quantidade),
      precoUnitario: Number(p.precoUnitario),
    })),
  };

  const response = await fetch(`${API_BASE_URL}/propostas`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(propostaBody),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Erro ao criar proposta: ${errorText}`);
  }

  return await response.json();
};
