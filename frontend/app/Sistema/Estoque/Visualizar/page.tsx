"use client";

import { useState, ChangeEvent } from "react";
import { useEstoque } from "./hooks/useEstoque";
import { Wrapper, Table, Th, Td, Header, Title, SearchContainer, SearchInput } from "./styles";

export default function VisualizarEstoquePage() {
  const { estoque, loading, error } = useEstoque();
  const [filtro, setFiltro] = useState("");

  const handleFiltroChange = (e: ChangeEvent<HTMLInputElement>) => {
    setFiltro(e.target.value);
  };

  const filteredEstoque = estoque.filter(
    (item) =>
      item.produtoNome?.toLowerCase().includes(filtro.toLowerCase()) ||
      item.produtoCodigo?.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <Wrapper>
      <Header>
        <Title>📦 Visualização de Estoque</Title>
      </Header>

      <SearchContainer>
        <SearchInput
          type="text"
          placeholder="Filtrar por código ou nome..."
          value={filtro}
          onChange={handleFiltroChange}
        />
      </SearchContainer>

      {loading && <p>Carregando...</p>}
      {error && <p className="text-red-500">{error}</p>}
      {!loading && !error && filteredEstoque.length === 0 && <p>Nenhum item encontrado.</p>}

      {!loading && filteredEstoque.length > 0 && (
        <Table>
          <thead>
            <tr>
              <Th>Código</Th>
              <Th>Produto</Th>
              <Th>Unidades</Th>
              <Th>Versão</Th>
              <Th>Última Atualização</Th>
            </tr>
          </thead>
          <tbody>
            {filteredEstoque.map((item) => (
              <tr key={item.produtoId}>
                <Td>{item.produtoCodigo}</Td>
                <Td>{item.produtoNome}</Td>
                <Td>{item.saldo}</Td>
                <Td>{item.versao}</Td>
                <Td>{new Date(item.atualizadoEm).toLocaleString("pt-BR")}</Td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </Wrapper>
  );
}
