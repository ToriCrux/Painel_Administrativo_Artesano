import { useEffect, useState } from "react";
import { getProdutos, updateProduto, deleteProduto, Produto } from "@/app/API/Produtos/apiServiceProduto";

export function useEditarProduto() {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [editando, setEditando] = useState<Produto | null>(null);
  const [mensagem, setMensagem] = useState<string | null>(null);

  const carregarProdutos = async () => {
    setLoading(true);
    try {
      const data = await getProdutos();
      setProdutos(data);
      setError(null);
    } catch {
      setError("Erro ao carregar produtos");
    } finally {
      setLoading(false);
    }
  };

  // ✅ Agora carrega os produtos automaticamente ao montar a página
  useEffect(() => {
    carregarProdutos();
  }, []);

  const handleSalvar = async (produto: Produto) => {
    try {
      await updateProduto(produto.id, {
        codigo: produto.codigo,
        nome: produto.nome,
        categoriaNome: produto.categoria?.nome || "",
        corIds: produto.corIds || [],
        medidas: produto.medidas,
        precoUnitario: produto.precoUnitario,
        ativo: produto.ativo,
        descricao: produto.descricao,
      });

      setMensagem("Produto atualizado com sucesso!");
      setEditando(null);
      await carregarProdutos();
    } catch {
      setMensagem("Erro ao atualizar produto!");
    }
  };

  const handleExcluir = async (id: number) => {
    try {
      await deleteProduto(id);
      setMensagem("Produto excluído com sucesso!");
      await carregarProdutos();
    } catch {
      setMensagem("Erro ao excluir produto!");
    }
  };

  return {
    produtos,
    loading,
    error,
    editando,
    setEditando,
    handleSalvar,
    handleExcluir,
    carregarProdutos,
    mensagem,
    setMensagem,
  };
}
