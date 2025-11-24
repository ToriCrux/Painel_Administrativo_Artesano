"use client";

import { useState, useEffect, ChangeEvent } from "react";
import { getEstoque, EstoqueItem } from "@/app/API/Estoque/estoque.api";
import { getMovimentacoesEstoque, MovimentacaoEstoque } from "@/app/API/Estoque/movimentacoes.api";
import {
  Wrapper,
  Header,
  Title,
  SearchContainer,
  SearchInput,
  Dropdown,
  DropdownItem,
  MovimentacaoTable,
  Th,
  Td,
  SubTitle,
  BackButton,
  NoResults,
} from "./styles";

export default function EstoqueComBuscaPage() {
  const [estoques, setEstoques] = useState<EstoqueItem[]>([]);
  const [filtro, setFiltro] = useState("");
  const [mostrarDropdown, setMostrarDropdown] = useState(false);
  const [estoqueSelecionado, setEstoqueSelecionado] = useState<EstoqueItem | null>(null);
  const [movimentacoes, setMovimentacoes] = useState<MovimentacaoEstoque[]>([]);
  const [loadingEstoque, setLoadingEstoque] = useState(false);
  const [loadingMov, setLoadingMov] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 🧾 Buscar estoques
  const fetchEstoques = async () => {
    try {
      setLoadingEstoque(true);
      const data = await getEstoque();
      setEstoques(data.content);
    } catch {
      setError("Erro ao carregar estoques.");
    } finally {
      setLoadingEstoque(false);
    }
  };

  // 🔍 Buscar movimentações
  const fetchMovimentacoes = async (produtoId: number) => {
    try {
      setLoadingMov(true);
      const data = await getMovimentacoesEstoque(produtoId);
      setMovimentacoes(data);
    } catch {
      setError("Erro ao buscar movimentações.");
    } finally {
      setLoadingMov(false);
    }
  };

  const handleSelecionarProduto = (item: EstoqueItem) => {
    setEstoqueSelecionado(item);
    setFiltro(item.produtoNome || "");
    setMostrarDropdown(false);
    fetchMovimentacoes(item.produtoId);
  };

  const handleVoltar = () => {
    setEstoqueSelecionado(null);
    setMovimentacoes([]);
    setFiltro("");
  };

  const produtosFiltrados = estoques.filter(
    (item) =>
      item.produtoNome?.toLowerCase().includes(filtro.toLowerCase()) ||
      item.produtoCodigo?.toLowerCase().includes(filtro.toLowerCase())
  );

  useEffect(() => {
    fetchEstoques();
  }, []);

  return (
    <Wrapper>
      <Header>
        <Title>📦 Movimentações de Estoque</Title>
      </Header>

      {error && <p className="text-red-500 mb-3">{error}</p>}

      <SearchContainer>
        <SearchInput
          type="text"
          placeholder="Pesquisar produto por nome ou código..."
          value={filtro}
          onFocus={() => setMostrarDropdown(true)}
          onChange={(e: ChangeEvent<HTMLInputElement>) => {
            setFiltro(e.target.value);
            setMostrarDropdown(true);
          }}
          disabled={!!estoqueSelecionado}
        />

        {/* Dropdown interativo */}
        {!estoqueSelecionado && mostrarDropdown && (
          <Dropdown>
            {loadingEstoque ? (
              <DropdownItem>Carregando...</DropdownItem>
            ) : produtosFiltrados.length > 0 ? (
              produtosFiltrados.map((item) => (
                <DropdownItem
                  key={item.produtoId}
                  onClick={() => handleSelecionarProduto(item)}
                >
                  <span className="font-medium text-teal-700">{item.produtoCodigo}</span> —{" "}
                  {item.produtoNome}
                </DropdownItem>
              ))
            ) : (
              <NoResults>Nenhum produto encontrado</NoResults>
            )}
          </Dropdown>
        )}
      </SearchContainer>

      {estoqueSelecionado && (
        <div className="mt-8 animate-fadeIn">
          <div className="flex justify-between items-center mb-3">
            <SubTitle>
              📊 Movimentações — {estoqueSelecionado.produtoNome} (
              {estoqueSelecionado.produtoCodigo})
            </SubTitle>
            <BackButton onClick={handleVoltar}>← Fechar</BackButton>
          </div>

          {loadingMov && <p>Carregando movimentações...</p>}
          {!loadingMov && movimentacoes.length === 0 && <p>Nenhuma movimentação encontrada.</p>}

          {!loadingMov && movimentacoes.length > 0 && (
            <MovimentacaoTable>
              <thead>
                <tr>
                  <Th>ID</Th>
                  <Th>Tipo</Th>
                  <Th>Quantidade</Th>
                  <Th>Saldo Anterior</Th>
                  <Th>Saldo Novo</Th>
                  <Th>Data</Th>
                </tr>
              </thead>
              <tbody>
                {movimentacoes.map((mov) => (
                  <tr key={mov.id}>
                    <Td>{mov.id}</Td>
                    <Td>{mov.tipo}</Td>
                    <Td>{mov.quantidade}</Td>
                    <Td>{mov.saldoAnterior}</Td>
                    <Td>{mov.saldoNovo}</Td>
                    <Td>{new Date(mov.criadoEm).toLocaleString("pt-BR")}</Td>
                  </tr>
                ))}
              </tbody>
            </MovimentacaoTable>
          )}
        </div>
      )}
    </Wrapper>
  );
}
