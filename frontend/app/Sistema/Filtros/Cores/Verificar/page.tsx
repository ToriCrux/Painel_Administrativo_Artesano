"use client";

import { Wrapper, HeaderBar, Breadcrumb } from "../../styles";
import { useVerificarCores } from "../hooks/useVerificarCores";



export default function VerificarCoresPage() {
  const { cores, loading, error } = useVerificarCores();

  if (loading)
    return (
      <Wrapper className="flex justify-center items-center min-h-[60vh]">
        <p className="text-gray-500 text-lg">Carregando cores...</p>
      </Wrapper>
    );

  if (error)
    return (
      <Wrapper className="flex justify-center items-center min-h-[60vh]">
        <p className="text-red-500 font-medium">Erro: {error}</p>
      </Wrapper>
    );

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>FILTROS / CORES / VERIFICAR</Breadcrumb>
      </HeaderBar>

      <div className="overflow-x-auto">
        <table className="w-full border-collapse bg-white shadow-md rounded-lg overflow-hidden text-sm sm:text-base">
          <thead className="bg-[#36B0AC] text-white">
            <tr>
              <th className="px-4 py-3 text-left">ID</th>
              <th className="px-4 py-3 text-left">Nome</th>
              <th className="px-4 py-3 text-left">Cor</th>
              <th className="px-4 py-3 text-left">Status</th>
              <th className="px-4 py-3 text-left hidden sm:table-cell">Criado em</th>
            </tr>
          </thead>
          <tbody>
            {cores.length === 0 ? (
              <tr>
                <td colSpan={5} className="text-center py-6 text-gray-500">
                  Nenhuma cor encontrada.
                </td>
              </tr>
            ) : (
              cores.map((cor) => (
                <tr key={cor.id} className="border-b hover:bg-gray-50 transition text-black">
                  <td className="px-4 py-3">{cor.id}</td>
                  <td className="px-4 py-3 font-medium">{cor.nome}</td>
                  <td className="px-4 py-3 flex items-center gap-2">
                    <div
                      className="w-5 h-5 rounded-full border"
                      style={{ backgroundColor: cor.hex }}
                    ></div>
                    <span className="text-black">{cor.hex}</span>
                  </td>
                  <td className="px-4 py-3">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-semibold ${
                        cor.ativo
                          ? "bg-green-100 text-green-700"
                          : "bg-red-100 text-red-600"
                      }`}
                    >
                      {cor.ativo ? "Ativo" : "Inativo"}
                    </span>
                  </td>
                  <td className="px-4 py-3 hidden sm:table-cell text-black">
                    {new Date(cor.criadoEm).toLocaleDateString("pt-BR")}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </Wrapper>
  );
}
