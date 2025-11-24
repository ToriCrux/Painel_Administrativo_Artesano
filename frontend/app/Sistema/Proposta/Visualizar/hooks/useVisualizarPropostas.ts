"use client";

import { useEffect, useState } from "react";
import { getPropostas, Proposta } from "@/app/API/Proposta/propostaListApi";

export const useVisualizarPropostas = () => {
  const [propostas, setPropostas] = useState<Proposta[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [filtro, setFiltro] = useState<string>("");

  const carregarPropostas = async () => {
    setLoading(true);
    try {
      const data = await getPropostas();
      setPropostas(data);
    } catch (error) {
      console.error("Erro ao carregar propostas:", error);
    } finally {
      setLoading(false);
    }
  };

  const propostasFiltradas = propostas.filter(
    (p) =>
      p.codigo.toLowerCase().includes(filtro.toLowerCase()) ||
      p.cliente?.nome?.toLowerCase().includes(filtro.toLowerCase())
  );

  useEffect(() => {
    carregarPropostas();
  }, []);

  return { propostas, propostasFiltradas, filtro, setFiltro, loading };
};
