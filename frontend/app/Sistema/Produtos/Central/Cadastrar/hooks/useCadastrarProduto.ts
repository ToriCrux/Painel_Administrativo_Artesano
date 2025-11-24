import { useState, useEffect } from "react";
import { criarProduto, ProdutoPayload } from "@/app/API/Produtos/apiServiceProduto";
import { getCategorias, Categoria } from "@/app/API/Filtros/Categoria/apiServiceCategoria";
import { getCores, Cor } from "@/app/API/Filtros/Cor/apiServiceCor";

export function useCadastrarProduto() {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [cores, setCores] = useState<Cor[]>([]);

  useEffect(() => {
    const carregarListas = async () => {
      try {
        const [catData, corData] = await Promise.all([
          getCategorias(),
          getCores(),
        ]);
        setCategorias(catData);
        setCores(corData);
      } catch (err) {
        console.error("Erro ao carregar listas:", err);
      }
    };

    carregarListas();
  }, []);

  const cadastrarProduto = async (dados: ProdutoPayload) => {
    try {
      setLoading(true);
      setError(null);
      await criarProduto(dados);
      setSuccess(true);
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError("Erro desconhecido ao cadastrar produto.");
    } finally {
      setLoading(false);
    }
  };

  return {
    cadastrarProduto,
    loading,
    success,
    error,
    setSuccess,
    categorias,
    cores,
  };
}
