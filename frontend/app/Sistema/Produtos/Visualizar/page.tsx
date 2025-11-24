"use client";

import { Wrapper, HeaderBar, Breadcrumb } from "../styles";
import { useRouter } from "next/navigation";
import Select from "react-select";
import { useVisualizarProdutos } from "./hooks/useVisualizarProdutos";

export default function VisualizarProdutosPage() {
  const router = useRouter();
  const {
    produtos,
    loading,
    error,
    filtrarPorNome,
  } = useVisualizarProdutos(); 

  const nomeOptions = produtos.map((p) => ({
    value: p.id,
    label: p.nome,
  }));

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>PRODUTOS / VISUALIZAR PRODUTOS</Breadcrumb>
      </HeaderBar>

      <div className="max-w-6xl mx-auto mt-6">
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-6">
            <Select
            options={nomeOptions}
            onChange={(option) => filtrarPorNome(option?.label || "")}
            placeholder="Pesquisar por nome..."
            className="text-black"
            styles={{
                menuList: (base) => ({
                ...base,
                maxHeight: 160, 
                overflowY: "auto",
                }),
            }}
            />
        </div>

        {loading ? (
          <p className="text-center text-gray-500">Carregando produtos...</p>
        ) : error ? (
          <p className="text-center text-red-500">{error}</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full border-collapse bg-white shadow-md rounded-lg overflow-hidden text-sm sm:text-base">
              <thead className="bg-[#36B0AC] text-white">
                <tr>
                  <th className="px-4 py-3 text-left">Código</th>
                  <th className="px-4 py-3 text-left">Nome</th>
                  <th className="px-4 py-3 text-left">Categoria</th>
                  <th className="px-4 py-3 text-left">Medidas</th>
                  <th className="px-4 py-3 text-left">Preço Unitário</th>
                  <th className="px-4 py-3 text-left">Cores</th>
                </tr>
              </thead>
              <tbody>
                {produtos.length === 0 ? (
                  <tr>
                    <td
                      colSpan={6}
                      className="text-center py-6 text-gray-500 font-medium"
                    >
                      Nenhum produto encontrado.
                    </td>
                  </tr>
                ) : (
                  produtos.map((produto) => (
                    <tr
                      key={produto.id}
                      className="border-b hover:bg-gray-50 transition text-black"
                    >
                      <td className="px-4 py-3">{produto.codigo}</td>
                      <td className="px-4 py-3">{produto.nome}</td>
                      <td className="px-4 py-3">{produto.categoria.nome}</td>
                      <td className="px-4 py-3">{produto.medidas}</td>
                      <td className="px-4 py-3">
                        R$ {produto.precoUnitario.toFixed(2)}
                      </td>
                        <td className="px-4 py-3">
                          {produto.cores && produto.cores.length > 0 ? (
                            produto.cores.map((cor) => (
                              <span
                                key={cor.id}
                                className="inline-flex items-center gap-2 px-2 py-1 border rounded-md text-xs mr-2"
                                style={{
                                  backgroundColor: cor.hex.startsWith("#") ? cor.hex : `#${cor.hex}`,
                                  color: "#fff",
                                }}
                              >
                                <span
                                  className="inline-block w-3 h-3 rounded-full border border-gray-300"
                                  style={{
                                    backgroundColor: cor.hex.startsWith("#")
                                      ? cor.hex
                                      : `#${cor.hex}`,
                                  }}
                                />
                                {cor.nome} ({cor.hex.startsWith("#") ? cor.hex : `#${cor.hex}`})
                              </span>
                            ))
                          ) : (
                            <span className="text-gray-400">Sem cores</span>
                          )}
                        </td>

                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}

        <div className="flex justify-center mt-8">
          <button
            onClick={() => router.push("/Sistema/Produtos")}
            className="px-6 py-2 rounded-md bg-gray-200 text-gray-700 font-medium hover:bg-gray-300 transition"
          >
            ← Voltar
          </button>
        </div>
      </div>
    </Wrapper>
  );
}
