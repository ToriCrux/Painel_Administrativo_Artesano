"use client";

import { useState, useEffect } from "react";
import { Cor, getCores, deleteCor, updateCor } from "@/app/API/Filtros/Cor/apiServiceCor";

export const useGerenciarCores = () => {
  const [cores, setCores] = useState<Cor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [selectedCor, setSelectedCor] = useState<Cor | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [editLoading, setEditLoading] = useState(false);

    useEffect(() => {
        carregarCores();
    }, []);

    const carregarCores = async () => {
    try {
        const data = await getCores();
        setCores(data);
    } catch (err: unknown) {
        if (err instanceof Error) {
        setError(err.message);
        } else {
        setError("Erro ao carregar cores.");
        }
    } finally {
        setLoading(false);
    }
    };


    const abrirModal = (cor: Cor) => {
        setSelectedCor(cor);
        setModalOpen(true);
    };

    const fecharModal = () => {
        setModalOpen(false);
        setSelectedCor(null);
    };

    const confirmarExclusao = async () => {
    if (!selectedCor) return;
    try {
        setDeleting(true);
        await deleteCor(selectedCor.id);
        setCores((prev) => prev.filter((c) => c.id !== selectedCor.id));
        fecharModal();
    } catch (err: unknown) {
        if (err instanceof Error) {
        alert(err.message);
        } else {
        alert("Erro ao excluir cor.");
        }
    } finally {
        setDeleting(false);
    }
    };


    const abrirEditModal = (cor: Cor) => {
        setSelectedCor(cor);
        setEditModalOpen(true);
    };

    const fecharEditModal = () => {
        setEditModalOpen(false);
        setSelectedCor(null);
    };

    const salvarEdicao = async () => {
    if (!selectedCor) return;
    try {
        setEditLoading(true);
        await updateCor(selectedCor.id, {
        nome: selectedCor.nome,
        hex: selectedCor.hex,
        ativo: selectedCor.ativo,
        });
        await carregarCores();
        fecharEditModal();
    } catch (err: unknown) {
        if (err instanceof Error) {
        alert(err.message);
        } else {
        alert("Erro ao atualizar cor.");
        }
    } finally {
        setEditLoading(false);
    }
    };


    return {
        cores,
        loading,
        error,
        modalOpen,
        editModalOpen,
        selectedCor,
        abrirModal,
        fecharModal,
        confirmarExclusao,
        deleting,
        abrirEditModal,
        fecharEditModal,
        salvarEdicao,
        editLoading,
        setSelectedCor,
    };
};
