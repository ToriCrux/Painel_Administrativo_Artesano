"use client";
import { useEffect, useState } from "react";
import { getEstoque, EstoqueItem } from "@/app/API/Estoque/estoque.api";

export interface EstoqueComProduto extends EstoqueItem {
  produtoNome: string;
  produtoCodigo: string;
}

export const useAtualizarEstoque = () => {
  const [estoque, setEstoque] = useState<EstoqueComProduto[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchEstoque = async () => {
    try {
      setLoading(true);
      const estoqueData = await getEstoque();

      // Agora o backend já traz produtoCodigo e produtoNome
      const estoqueComProdutos = estoqueData.content.map((item) => ({
        produtoId: item.produtoId,
        produtoCodigo: item.produtoCodigo || "-",
        produtoNome: item.produtoNome || "Produto não encontrado",
        saldo: item.saldo,
        versao: item.versao,
        atualizadoEm: item.atualizadoEm,
      }));

      setEstoque(estoqueComProdutos);
    } catch (err) {
      if (err instanceof Error) setError(err.message);
      else setError("Erro ao buscar estoque.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEstoque();
  }, []);

  return { estoque, loading, error, refetch: fetchEstoque };
};
