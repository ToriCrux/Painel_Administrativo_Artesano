"use client";

import { useState } from "react";
import { criarCor } from "@/app/API/Filtros/Cor/apiServiceCor";

export function useAdicionarCor() {
  const [nome, setNome] = useState("");
  const [hex, setHex] = useState("#000000");
  const [loading, setLoading] = useState(false);
  const [mensagem, setMensagem] = useState("");
  const [erro, setErro] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensagem("");
    setErro("");

    if (!nome || !hex) {
      setErro("Preencha todos os campos.");
      return;
    }

    try {
      setLoading(true);
      await criarCor({ nome, hex, ativo: true });
      setMensagem("Cor adicionada com sucesso!");
      setNome("");
      setHex("#000000");
    } catch (error) {
      if (error instanceof Error) {
        setErro(error.message);
      } else {
        setErro("Erro inesperado ao adicionar a cor.");
      }
    } finally {
      setLoading(false);
    }
  };

  return {
    nome,
    setNome,
    hex,
    setHex,
    loading,
    mensagem,
    erro,
    handleSubmit,
  };
}
