"use client";

import { Wrapper, HeaderBar, Breadcrumb } from "../../styles";
import { Pencil, Trash2 } from "lucide-react";
import { useEditarProduto } from "./hooks/useEditarProduto";
import ModalEditarProduto from "./hooks/ModalEditarProduto";
import ModalMensagem from "./hooks/ModalMensagem"; 

export default function EditarProdutoPage() {
  const {
    produtos,
    loading,
    error,
    editando,
    setEditando,
    handleExcluir,
    handleSalvar,
    mensagem,
    setMensagem,
  } = useEditarProduto();

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>PRODUTOS / CENTRAL DE PRODUTOS / EDITAR PRODUTO</Breadcrumb>
      </HeaderBar>

      <div className="max-w-6xl mx-auto mt-8 text-black">
        {loading && <p className="text-center">Carregando produtos...</p>}
        {error && <p className="text-center text-red-500">{error}</p>}

        {!loading && !error && (
          <table className="w-full border-collapse bg-white shadow-md rounded-lg overflow-hidden text-center">
            <thead className="bg-[#36B0AC] text-white">
              <tr>
                <th className="px-4 py-2">Código</th>
                <th className="px-4 py-2">Nome</th>
                <th className="px-4 py-2">Categoria</th>
                <th className="px-4 py-2">Preço</th>
                <th className="px-4 py-2">Ações</th>
              </tr>
            </thead>
            <tbody>
              {produtos.length === 0 ? (
                <tr>
                  <td
                    colSpan={5}
                    className="text-center py-4 text-gray-500 font-medium"
                  >
                    Nenhum produto encontrado.
                  </td>
                </tr>
              ) : (
                produtos.map((p) => (
                  <tr
                    key={p.id}
                    className="border-b hover:bg-gray-50 transition"
                  >
                    <td className="px-4 py-2">{p.codigo}</td>
                    <td className="px-4 py-2">{p.nome}</td>
                    <td className="px-4 py-2">
                      {p.categoria?.nome || p.categoriaNome}
                    </td>
                    <td className="px-4 py-2">
                      R$ {p.precoUnitario.toFixed(2)}
                    </td>
                    <td className="px-4 py-2 flex justify-center gap-3">
                      <button
                        onClick={() => setEditando(p)}
                        className="text-blue-600 hover:text-blue-800"
                      >
                        <Pencil size={20} />
                      </button>
                      <button
                        onClick={() => handleExcluir(p.id)}
                        className="text-red-600 hover:text-red-800"
                      >
                        <Trash2 size={20} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}

        {/* ✅ Modal de edição */}
        {editando && (
          <ModalEditarProduto
            editando={editando}
            setEditando={setEditando}
            handleSalvar={handleSalvar}
          />
        )}

        {/* ✅ Modal de mensagem de sucesso/erro */}
        {mensagem && (
          <ModalMensagem
            mensagem={mensagem}
            onClose={() => setMensagem(null)}
          />
        )}
      </div>
    </Wrapper>
  );
}
