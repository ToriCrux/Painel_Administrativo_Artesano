"use client";

import { useState, useEffect } from "react";
import { Categoria, getCategorias, deleteCategoria, updateCategoria } from "@/app/API/Filtros/Categoria/apiServiceCategoria";

export const useGerenciarCategorias = () => {
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [selectedCategoria, setSelectedCategoria] = useState<Categoria | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [editLoading, setEditLoading] = useState(false);

  useEffect(() => {
    carregarCategorias();
  }, []);

  const carregarCategorias = async () => {
    try {
      const data = await getCategorias();
      setCategorias(data);
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError("Erro ao carregar categorias.");
    } finally {
      setLoading(false);
    }
  };

  const abrirModal = (categoria: Categoria) => {
    setSelectedCategoria(categoria);
    setModalOpen(true);
  };

  const fecharModal = () => {
    setModalOpen(false);
    setSelectedCategoria(null);
  };

  const confirmarExclusao = async () => {
    if (!selectedCategoria) return;
    try {
      setDeleting(true);
      await deleteCategoria(selectedCategoria.id);
      setCategorias((prev) => prev.filter((c) => c.id !== selectedCategoria.id));
      fecharModal();
    } catch (err: unknown) {
      if (err instanceof Error) alert(err.message);
      else alert("Erro ao excluir categoria.");
    } finally {
      setDeleting(false);
    }
  };

  const abrirEditModal = (categoria: Categoria) => {
    setSelectedCategoria(categoria);
    setEditModalOpen(true);
  };

  const fecharEditModal = () => {
    setEditModalOpen(false);
    setSelectedCategoria(null);
  };

  const salvarEdicao = async () => {
    if (!selectedCategoria) return;
    try {
      setEditLoading(true);
      await updateCategoria(selectedCategoria.id, {
        nome: selectedCategoria.nome,
        ativo: selectedCategoria.ativo,
      });
      await carregarCategorias();
      fecharEditModal();
    } catch (err: unknown) {
      if (err instanceof Error) alert(err.message);
      else alert("Erro ao atualizar categoria.");
    } finally {
      setEditLoading(false);
    }
  };

  return {
    categorias,
    loading,
    error,
    modalOpen,
    editModalOpen,
    selectedCategoria,
    abrirModal,
    fecharModal,
    confirmarExclusao,
    deleting,
    abrirEditModal,
    fecharEditModal,
    salvarEdicao,
    editLoading,
    setSelectedCategoria,
  };
};
