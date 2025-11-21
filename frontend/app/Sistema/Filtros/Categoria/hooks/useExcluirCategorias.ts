"use client";

import { useEffect, useState } from "react";
import { getCategorias, deleteCategoria, Categoria } from "@/app/API/Filtros/Categoria/apiServiceCategoria";

export function useExcluirCategorias() {
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    carregarCategorias();
  }, []);

  const carregarCategorias = async () => {
    try {
      const data = await getCategorias();
      setCategorias(data);
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError("Erro desconhecido ao carregar categorias.");
    } finally {
      setLoading(false);
    }
  };

  const abrirModal = (id: number) => {
    setSelectedId(id);
    setModalOpen(true);
  };

  const fecharModal = () => {
    setSelectedId(null);
    setModalOpen(false);
  };

  const confirmarExclusao = async () => {
    if (!selectedId) return;

    try {
      setDeleting(true);
      await deleteCategoria(selectedId);
      setCategorias((prev) => prev.filter((c) => c.id !== selectedId));
      fecharModal();
    } catch (err: unknown) {
      if (err instanceof Error) alert(err.message);
      else alert("Erro desconhecido ao excluir.");
    } finally {
      setDeleting(false);
    }
  };

  return {
    categorias,
    loading,
    error,
    modalOpen,
    deleting,
    abrirModal,
    fecharModal,
    confirmarExclusao,
  };
}
