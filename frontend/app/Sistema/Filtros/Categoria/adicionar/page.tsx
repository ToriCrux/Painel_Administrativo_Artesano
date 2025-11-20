"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Wrapper, HeaderBar, Breadcrumb } from "../../styles";
import { criarCategoria } from "@/app/API/Filtros/Categoria/apiServiceCategoria";


export default function AdicionarCategoriaPage() {
  const [nome, setNome] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!nome.trim()) {
      alert("O campo nome é obrigatório.");
      return;
    }

    try {
      setLoading(true);

      await criarCategoria({ nome, ativo: true });

      alert("Categoria criada com sucesso!");
      router.push("/Sistema/Filtros/Categoria"); 
    } catch (error) {
      alert("Erro ao criar categoria. Verifique o console.");
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>FILTROS / CATEGORIA / ADICIONAR</Breadcrumb>
      </HeaderBar>

      <form
        onSubmit={handleSubmit}
        className="max-w-md mx-auto bg-white shadow-md rounded-lg p-6 flex flex-col gap-4"
      >
        <label className="text-gray-700 font-medium">Nome da Categoria:</label>
        <input
          type="text"
          value={nome}
          onChange={(e) => setNome(e.target.value)}
          placeholder="Ex: Categoria teste 2"
          className="border border-gray-300 text-black rounded-md p-2 focus:ring-2 focus:ring-teal-400 outline-none"
        />

        <button
          type="submit"
          disabled={loading}
          className="bg-[#00B3A4] text-white font-semibold py-2 px-4 rounded-md hover:opacity-90 transition disabled:opacity-50 cursor-pointer"
        >
          {loading ? "Salvando..." : "Adicionar"}
        </button>

        <button
          type="button"
          onClick={() => router.push("/Sistema/Filtros/Categoria")}
          className="mt-2 text-gray-600 hover:text-teal-600 transition cursor-pointer" 
        >
          ← Voltar
        </button>
      </form>
    </Wrapper>
  );
}
