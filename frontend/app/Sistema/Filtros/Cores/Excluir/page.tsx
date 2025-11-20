"use client";

import { Wrapper, HeaderBar, Breadcrumb } from "../../styles";
import { Trash2, X, Pencil } from "lucide-react";
import { useGerenciarCores } from "../hooks/useGerenciarCores";

export default function ExcluirCoresPage() {
  const {
    cores,
    loading,
    error,
    modalOpen,
    editModalOpen,
    selectedCor,
    abrirModal,
    fecharModal,
    confirmarExclusao,
    deleting,
    abrirEditModal,
    fecharEditModal,
    salvarEdicao,
    editLoading,
    setSelectedCor,
  } = useGerenciarCores();

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
        <Breadcrumb>FILTROS / CORES / GERENCIAR</Breadcrumb>
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
              <th className="px-4 py-3 text-center">Ações</th>
            </tr>
          </thead>
          <tbody>
            {cores.length === 0 ? (
              <tr>
                <td colSpan={6} className="text-center py-6 text-gray-500">
                  Nenhuma cor encontrada.
                </td>
              </tr>
            ) : (
              cores.map((cor) => (
                <tr key={cor.id} className="border-b hover:bg-gray-50 transition text-black">
                  <td className="px-4 py-3">{cor.id}</td>
                  <td className="px-4 py-3 font-medium">{cor.nome}</td>
                  <td className="px-4 py-3 flex items-center gap-2">
                    <div className="w-5 h-5 rounded-full border" style={{ backgroundColor: cor.hex }}></div>
                    <span>{cor.hex}</span>
                  </td>
                  <td className="px-4 py-3">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-semibold ${
                        cor.ativo ? "bg-green-100 text-green-700" : "bg-red-100 text-red-600"
                      }`}
                    >
                      {cor.ativo ? "Ativo" : "Inativo"}
                    </span>
                  </td>
                  <td className="px-4 py-3 hidden sm:table-cell">
                    {new Date(cor.criadoEm).toLocaleDateString("pt-BR")}
                  </td>
                  <td className="px-4 py-3 text-center flex justify-center gap-3">
                    <button
                      onClick={() => abrirEditModal(cor)}
                      className="text-[#36B0AC] hover:text-[#2a8a87] transition cursor-pointer"
                      title="Editar cor"
                    >
                      <Pencil size={20} />
                    </button>
                    <button
                      onClick={() => abrirModal(cor)}
                      className="text-red-600 hover:text-red-800 transition cursor-pointer"
                      title="Excluir cor"
                    >
                      <Trash2 size={20} />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Modal de Exclusão */}
      {modalOpen && selectedCor && (
        <div className="fixed inset-0 flex justify-center items-center bg-black/30 backdrop-blur-sm z-50">
          <div className="bg-white rounded-lg shadow-lg p-6 w-[90%] max-w-md text-center relative">
            <button
              onClick={fecharModal}
              className="absolute top-3 right-3 text-gray-400 hover:text-gray-600 cursor-pointer"
            >
              <X size={20} />
            </button>

            <h2 className="text-xl font-semibold text-[#36B0AC] mb-4">Confirmar exclusão</h2>
            <p className="text-gray-700 mb-6">
              Tem certeza que deseja excluir a cor <strong>{selectedCor.nome}</strong>?
            </p>

            <div className="flex justify-center gap-4">
              <button
                onClick={fecharModal}
                className="px-5 py-2 rounded-lg border border-gray-300 text-gray-600 hover:bg-gray-100 transition cursor-pointer"
                disabled={deleting}
              >
                Cancelar
              </button>
              <button
                onClick={confirmarExclusao}
                disabled={deleting}
                className="px-5 py-2 rounded-lg bg-[#36B0AC] text-white font-medium hover:opacity-90 transition disabled:opacity-50 cursor-pointer"
              >
                {deleting ? "Excluindo..." : "Confirmar"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de Edição */}
      {editModalOpen && selectedCor && (
        <div className="fixed inset-0 flex justify-center items-center bg-black/30 backdrop-blur-sm z-50">
          <div className="bg-white rounded-lg shadow-lg p-6 w-[90%] max-w-md text-center relative">
            <button
              onClick={fecharEditModal}
              className="absolute top-3 right-3 text-gray-400 hover:text-gray-600 cursor-pointer"
            >
              <X size={20} />
            </button>

            <h2 className="text-xl font-semibold text-[#36B0AC] mb-4">Editar Cor</h2>

            <div className="flex flex-col gap-4">
              <input
                type="text"
                value={selectedCor.nome}
                onChange={(e) => setSelectedCor({ ...selectedCor, nome: e.target.value })}
                placeholder="Nome da cor"
                className="border rounded-md px-3 py-2 w-full text-black"
              />
              <input
                type="text"
                value={selectedCor.hex}
                onChange={(e) => setSelectedCor({ ...selectedCor, hex: e.target.value })}
                placeholder="Valor HEX (ex: #FF0000)"
                className="border rounded-md px-3 py-2 w-full text-black"
              />
              <label className="flex items-center justify-center gap-2 text-black">
                <input
                  type="checkbox"
                  checked={selectedCor.ativo}
                  onChange={(e) => setSelectedCor({ ...selectedCor, ativo: e.target.checked })}
                />
                Ativo
              </label>
            </div>

            <div className="flex justify-center gap-4 mt-6">
              <button
                onClick={fecharEditModal}
                className="px-5 py-2 rounded-lg border border-gray-300 text-gray-600 hover:bg-gray-100 transition cursor-pointer"
              >
                Cancelar
              </button>
              <button
                onClick={salvarEdicao}
                disabled={editLoading}
                className="px-5 py-2 rounded-lg bg-[#36B0AC] text-white font-medium hover:opacity-90 transition disabled:opacity-50 cursor-pointer"
              >
                {editLoading ? "Salvando..." : "Salvar"}
              </button>
            </div>
          </div>
        </div>
      )}
    </Wrapper>
  );
}
