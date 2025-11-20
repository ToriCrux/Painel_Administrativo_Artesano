"use client";

import { Wrapper, HeaderBar, Breadcrumb } from "../../styles";
import { useVerificarCategorias } from "../hooks/useVerificarCategorias";


export default function VerificarCategoriasPage() {
  const { categorias, loading, error } = useVerificarCategorias();

  if (loading)
    return (
      <Wrapper className="flex justify-center items-center min-h-[60vh]">
        <p className="text-gray-500 text-lg">Carregando categorias...</p>
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
        <Breadcrumb>FILTROS / CATEGORIA / VERIFICAR</Breadcrumb>
      </HeaderBar>

      <div className="overflow-x-auto">
        <table className="w-full border-collapse bg-white shadow-md rounded-lg overflow-hidden text-sm sm:text-base">
          <thead className="bg-[#36B0AC] text-white">
            <tr>
              <th className="px-4 py-3 text-left">ID</th>
              <th className="px-4 py-3 text-left">Nome</th>
              <th className="px-4 py-3 text-left">Status</th>
              <th className="px-4 py-3 text-left hidden sm:table-cell">Criado em</th>
            </tr>
          </thead>
          <tbody>
            {categorias.length === 0 ? (
              <tr>
                <td colSpan={4} className="text-center py-6 text-gray-500">
                  Nenhuma categoria encontrada.
                </td>
              </tr>
            ) : (
              categorias.map((cat) => (
                <tr
                  key={cat.id}
                  className="border-b hover:bg-gray-50 transition text-black"
                >
                  <td className="px-4 py-3">{cat.id}</td>
                  <td className="px-4 py-3 font-medium">{cat.nome}</td>
                  <td className="px-4 py-3">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-semibold ${
                        cat.ativo
                          ? "bg-green-100 text-green-700"
                          : "bg-red-100 text-red-600"
                      }`}
                    >
                      {cat.ativo ? "Ativo" : "Inativo"}
                    </span>
                  </td>
                  <td className="px-4 py-3 hidden sm:table-cell text-black">
                    {new Date(cat.criadoEm).toLocaleDateString("pt-BR")}
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
