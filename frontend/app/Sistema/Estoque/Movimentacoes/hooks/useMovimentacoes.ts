import { useCallback, useEffect, useState } from "react";
import {
  getMovimentacoesEstoque,
  MovimentacaoEstoque,
} from "@/app/API/Estoque/movimentacoes.api";

export const useMovimentacoes = (produtoId: number) => {
  const [movimentacoes, setMovimentacoes] = useState<MovimentacaoEstoque[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchMovimentacoes = useCallback(async () => {
    try {
      setLoading(true);
      const data = await getMovimentacoesEstoque(produtoId);
      setMovimentacoes(data);
    } catch (err) {
      if (err instanceof Error) setError(err.message);
      else setError("Erro desconhecido.");
    } finally {
      setLoading(false);
    }
  }, [produtoId]); 

  useEffect(() => {
    fetchMovimentacoes();
  }, [fetchMovimentacoes]); 
  return { movimentacoes, loading, error, refetch: fetchMovimentacoes };
};
