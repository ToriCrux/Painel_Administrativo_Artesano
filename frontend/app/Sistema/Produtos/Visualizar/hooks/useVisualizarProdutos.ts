"use client";

import { Produto, getProdutos, getProdutoById } from "@/app/API/Produtos/apiServiceProduto";
import { useEffect, useState } from "react";


export function useVisualizarProdutos() {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [filteredProdutos, setFilteredProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const fetchProdutos = async () => {
      setLoading(true);
      try {
        const data = await getProdutos();
        setProdutos(data);
        setFilteredProdutos(data);
      } catch {
        setError("Erro ao carregar produtos.");
      } finally {
        setLoading(false);
      }
    };

    fetchProdutos();
  }, []);

  const filtrarPorNome = (nome: string) => {
    setSearchTerm(nome);
    const filtered = produtos.filter((p) =>
      p.nome.toLowerCase().includes(nome.toLowerCase())
    );
    setFilteredProdutos(filtered);
  };

  const buscarProdutoPorId = async (id: number) => {
    setLoading(true);
    try {
      const produto = await getProdutoById(id);
      setFilteredProdutos([produto]);
    } catch {
      setError("Produto não encontrado.");
    } finally {
      setLoading(false);
    }
  };

  return {
    produtos: filteredProdutos,
    loading,
    error,
    searchTerm,
    filtrarPorNome,
    buscarProdutoPorId,
  };
}
