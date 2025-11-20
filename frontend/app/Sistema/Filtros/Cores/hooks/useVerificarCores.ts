"use client";

import { Cor, getCores } from "@/app/API/Filtros/Cor/apiServiceCor";
import { useEffect, useState } from "react";


export function useVerificarCores() {
  const [cores, setCores] = useState<Cor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
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

    carregarCores();
  }, []);

  return { cores, loading, error };
}
