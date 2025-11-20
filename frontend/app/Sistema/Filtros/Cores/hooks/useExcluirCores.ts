"use client";

import { useEffect, useState } from "react";
import { getCores, deleteCor, Cor } from "@/app/API/Filtros/Cor/apiServiceCor";

export function useExcluirCores() {
  const [cores, setCores] = useState<Cor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    carregarCores();
  }, []);

  const carregarCores = async () => {
    try {
      const data = await getCores();
      setCores(data);
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError("Erro desconhecido ao carregar cores.");
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
      await deleteCor(selectedId);
      setCores((prev) => prev.filter((c) => c.id !== selectedId));
      fecharModal();
    } catch (err: unknown) {
      console.error("Erro ao excluir cor:", err);
      alert("Erro ao excluir cor.");
    } finally {
      setDeleting(false);
    }
  };

  return {
    cores,
    loading,
    error,
    modalOpen,
    abrirModal,
    fecharModal,
    confirmarExclusao,
    deleting,
  };
}
