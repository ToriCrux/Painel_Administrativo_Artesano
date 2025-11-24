"use client";

import { useState, ChangeEvent } from "react";
import { useAtualizarEstoque } from "./hooks/useAtualizarEstoque";
import {
  Wrapper,
  Header,
  Title,
  SearchContainer,
  SearchInput,
  Table,
  Th,
  Td,
  EditIcon,
  DeleteIcon,
  ModalOverlay,
  ModalContent,
  ModalInput,
} from "./styles";
import {
  ajustarEstoque,
  entradaEstoque,
  saidaEstoque,
  deletarEstoque,
  EstoqueItem,
} from "@/app/API/Estoque/estoque.api";

export default function AtualizarEstoquePage() {
  const { estoque, loading, error, refetch } = useAtualizarEstoque();
  const [filtro, setFiltro] = useState("");
  const [produtoSelecionado, setProdutoSelecionado] = useState<
    (EstoqueItem & { produtoNome?: string; produtoCodigo?: string }) | null
  >(null);
  const [valor, setValor] = useState<number>(0);
  const [modo, setModo] = useState<"ajuste" | "entrada" | "saida" | null>(null);
  const [modalSucesso, setModalSucesso] = useState(false);
  const [modalExcluir, setModalExcluir] = useState<EstoqueItem | null>(null);

  const filtered = estoque.filter(
    (item) =>
      item.produtoNome?.toLowerCase().includes(filtro.toLowerCase()) ||
      item.produtoCodigo?.toLowerCase().includes(filtro.toLowerCase())
  );

  const handleFiltroChange = (e: ChangeEvent<HTMLInputElement>) => {
    setFiltro(e.target.value);
  };

  const abrirModal = (
    produto: EstoqueItem & { produtoNome?: string; produtoCodigo?: string }
  ) => {
    setProdutoSelecionado(produto);
    setModo(null);
    setValor(0);
  };

  const fecharModal = () => {
    setProdutoSelecionado(null);
    setValor(0);
    setModo(null);
  };

  const confirmar = async () => {
    if (!produtoSelecionado || !modo) return;

    const id = produtoSelecionado.produtoId;
    const payload = { saldo: valor };

    try {
      if (modo === "ajuste") await ajustarEstoque(id, payload);
      else if (modo === "entrada") await entradaEstoque(id, payload);
      else if (modo === "saida") await saidaEstoque(id, payload);

      setModalSucesso(true);
      fecharModal();
      refetch();

      setTimeout(() => {
        setModalSucesso(false);
      }, 3000);
    } catch (err) {
      console.error("Erro ao atualizar estoque:", err);
    }
  };

  const confirmarExclusao = async () => {
    if (!modalExcluir) return;
    try {
      await deletarEstoque(modalExcluir.produtoId);
      setModalExcluir(null);
      refetch();
    } catch (err) {
      console.error("Erro ao excluir estoque:", err);
    }
  };

  return (
    <Wrapper>
      <Header>
        <Title>🧾 Atualizar Estoque</Title>
      </Header>

      <SearchContainer>
        <SearchInput
          type="text"
          placeholder="Filtrar por nome ou código..."
          value={filtro}
          onChange={handleFiltroChange}
        />
      </SearchContainer>

      {loading && <p>Carregando...</p>}
      {error && <p className="text-red-500">{error}</p>}

      {!loading && filtered.length > 0 && (
        <Table>
          <thead>
            <tr>
              <Th>Código</Th>
              <Th>Produto</Th>
              <Th>Unidade</Th>
              <Th>Ações</Th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((item) => (
              <tr key={item.produtoId}>
                <Td>{item.produtoCodigo}</Td>
                <Td>{item.produtoNome}</Td>
                <Td>{item.saldo}</Td>
                <Td className="flex gap-3">
                  <EditIcon onClick={() => abrirModal(item)}>✏️</EditIcon>
                  <DeleteIcon onClick={() => setModalExcluir(item)}>🗑️</DeleteIcon>
                </Td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {/* === Modal de Edição === */}
      {produtoSelecionado && (
        <ModalOverlay>
          <ModalContent>
            <h2 className="text-lg font-semibold mb-3 text-gray-800 text-center">
              Atualizar{" "}
              <span className="text-teal-600">
                {produtoSelecionado.produtoNome}
              </span>
            </h2>

            {!modo ? (
              <>
                <p className="text-gray-600 text-sm mb-4 text-center">
                  Selecione o tipo de atualização que deseja realizar:
                </p>

                <div className="flex flex-col gap-3">
                  <button
                    onClick={() => setModo("ajuste")}
                    className="bg-teal-600 text-white py-2 rounded-md hover:bg-teal-700 transition"
                  >
                    🔧 Ajuste — Substitui o saldo atual pelo valor informado.
                  </button>

                  <button
                    onClick={() => setModo("entrada")}
                    className="bg-teal-600 text-white py-2 rounded-md hover:bg-teal-700 transition"
                  >
                    ➕ Entrada — Soma o valor informado ao saldo atual.
                  </button>

                  <button
                    onClick={() => setModo("saida")}
                    className="bg-teal-600 text-white py-2 rounded-md hover:bg-teal-700 transition"
                  >
                    ➖ Saída — Subtrai o valor informado do saldo atual.
                  </button>
                </div>

                <div className="flex justify-end mt-6">
                  <button
                    onClick={fecharModal}
                    className="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition"
                  >
                    Fechar
                  </button>
                </div>
              </>
            ) : (
              <>
                <p className="text-gray-600 text-sm mb-4 text-center">
                  {modo === "ajuste"
                    ? "Informe o novo saldo que substituirá o atual."
                    : modo === "entrada"
                    ? "Informe a quantidade que deseja adicionar ao estoque."
                    : "Informe a quantidade que deseja remover do estoque."}
                </p>

                <ModalInput
                  type="number"
                  value={valor}
                  onChange={(e: ChangeEvent<HTMLInputElement>) =>
                    setValor(Number(e.target.value))
                  }
                />

                <div className="flex justify-between mt-6">
                  <button
                    onClick={() => setModo(null)}
                    className="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition"
                  >
                    Voltar
                  </button>

                  <div className="flex gap-2">
                    <button
                      onClick={fecharModal}
                      className="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition"
                    >
                      Cancelar
                    </button>

                    <button
                      onClick={confirmar}
                      className="px-4 py-2 bg-teal-600 text-white rounded-lg hover:bg-teal-700 transition"
                    >
                      Confirmar
                    </button>
                  </div>
                </div>
              </>
            )}
          </ModalContent>
        </ModalOverlay>
      )}

      {/* === Modal de Exclusão === */}
      {modalExcluir && (
        <ModalOverlay>
          <ModalContent className="text-center">
            <h2 className="text-lg font-semibold text-red-600 mb-3">
              ⚠️ Confirmar Exclusão
            </h2>
            <p className="text-gray-700 mb-4">
              Tem certeza que deseja excluir o estoque de{" "}
              <span className="font-semibold">{modalExcluir.produtoNome}</span>?
            </p>
            <div className="flex justify-center gap-3">
              <button
                onClick={() => setModalExcluir(null)}
                className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 transition"
              >
                Cancelar
              </button>
              <button
                onClick={confirmarExclusao}
                className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition"
              >
                Excluir
              </button>
            </div>
          </ModalContent>
        </ModalOverlay>
      )}

      {/* === Modal de Sucesso === */}
      {modalSucesso && (
        <ModalOverlay>
          <ModalContent className="text-center">
            <h2 className="text-xl font-semibold text-teal-700 mb-3">
              ✅ Sucesso!
            </h2>
            <p className="text-gray-600 mb-6">
              O estoque foi atualizado com sucesso.
            </p>
            <button
              onClick={() => setModalSucesso(false)}
              className="px-5 py-2 bg-teal-600 text-white rounded-lg hover:bg-teal-700 transition"
            >
              Fechar
            </button>
          </ModalContent>
        </ModalOverlay>
      )}
    </Wrapper>
  );
}
