"use client";

import { Wrapper, HeaderBar, Breadcrumb } from "../../styles";
import { useAdicionarCor } from "../hooks/useAdicionarCor";


export default function AdicionarCorPage() {
  const { nome, setNome, hex, setHex, loading, mensagem, erro, handleSubmit } =
    useAdicionarCor();

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>FILTROS / CORES / ADICIONAR</Breadcrumb>
      </HeaderBar>

      <div className="max-w-lg mx-auto mt-10 bg-white shadow-md rounded-lg p-6">
        <h2 className="text-2xl font-semibold text-[#36B0AC] mb-6 text-center">
          Adicionar Nova Cor
        </h2>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-black font-medium mb-2">
              Nome da Cor
            </label>
            <input
              type="text"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              className="w-full text-black border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#36B0AC]"
              placeholder="Ex: Azul Claro"
            />
          </div>

          <div>
            <label className="block text-black font-medium mb-2">
              Código HEX
            </label>
            <div className="flex items-center gap-3">
              <input
                type="color"
                value={hex}
                onChange={(e) => setHex(e.target.value)}
                className="w-12 h-10 text-black border rounded-md cursor-pointer"
              />
              <input
                type="text"
                value={hex}
                onChange={(e) => setHex(e.target.value)}
                className="flex-1 border text-black rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#36B0AC]"
                placeholder="#FFFFFF"
              />
            </div>
          </div>

          {erro && <p className="text-red-500 text-sm">{erro}</p>}
          {mensagem && <p className="text-green-600 text-sm">{mensagem}</p>}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-[#36B0AC] text-white py-2 rounded-md font-medium hover:opacity-90 transition disabled:opacity-50 cursor-pointer"
          >
            {loading ? "Adicionando..." : "Adicionar Cor"}
          </button>
        </form>
      </div>
    </Wrapper>
  );
}
