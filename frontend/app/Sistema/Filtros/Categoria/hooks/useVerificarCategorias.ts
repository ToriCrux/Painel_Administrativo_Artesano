"use client";

import { useEffect, useState } from "react";
import { getCategorias, Categoria } from "@/app/API/Filtros/Categoria/apiServiceCategoria";

export function useVerificarCategorias() {
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

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

  return { categorias, loading, error };
}
