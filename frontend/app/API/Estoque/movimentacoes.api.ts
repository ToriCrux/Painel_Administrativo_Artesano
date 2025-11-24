import { getAuthToken } from "../Login/auth";

/* ============================
   🔹 Tipos
============================ */
export interface MovimentacaoEstoque {
  id: number;
  produtoId: number;
  tipo: string;
  quantidade: number;
  saldoAnterior: number;
  saldoNovo: number;
  criadoEm: string;
}

/* ============================
   🔹 Buscar Movimentações (GET)
============================ */
export const getMovimentacoesEstoque = async (
  produtoId: number
): Promise<MovimentacaoEstoque[]> => {
  const token = getAuthToken();
  if (!token) throw new Error("Usuário não autenticado.");

  const response = await fetch(
    `http://localhost:8082/api/v1/estoque/${produtoId}/movimentacoes`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!response.ok) throw new Error("Erro ao buscar movimentações.");

  return await response.json();
};
