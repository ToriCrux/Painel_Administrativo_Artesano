"use client";

import { useVisualizarPropostas } from "./hooks/useVisualizarPropostas";
import {
  Wrapper,
  HeaderBar,
  Breadcrumb,
  Section,
  Table,
  TableHeader,
  TableRow,
  TableCell,
  Input,
  Label,
  LinkButton,
} from "./styles";
import Link from "next/link";

export default function VisualizarPropostasPage() {
  const { propostasFiltradas, filtro, setFiltro, loading } =
    useVisualizarPropostas();

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>
          PROPOSTAS ORÇAMENTÁRIAS / VISUALIZAR PROPOSTA ORÇAMENTAL
        </Breadcrumb>
      </HeaderBar>

      <Section>
        <Label>Filtrar por Código ou Nome do Cliente:</Label>
        <Input
          type="text"
          placeholder="Digite para filtrar..."
          value={filtro}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => setFiltro(e.target.value)}
        />
      </Section>

      {loading ? (
        <p>Carregando propostas...</p>
      ) : (
        <Table>
          <thead>
            <TableRow>
              <TableHeader>Código</TableHeader>
              <TableHeader>Nome Cliente</TableHeader>
              <TableHeader>Produto</TableHeader>
              <TableHeader>Quantidades</TableHeader>
              <TableHeader>Preço Unitário</TableHeader>
              <TableHeader>Total</TableHeader>
              <TableHeader>Link</TableHeader>
            </TableRow>
          </thead>
          <tbody>
            {propostasFiltradas.map((p) => (
              <TableRow key={p.id}>
                <TableCell>{p.codigo}</TableCell>
                <TableCell>{p.cliente?.nome}</TableCell>
                <TableCell>
                  {p.produtos.map((prod) => prod.nomeProduto).join(", ")}
                </TableCell>
                <TableCell>
                  {p.produtos.map((prod) => prod.quantidade).join(", ")}
                </TableCell>
                <TableCell>
                  {p.produtos
                    .map((prod) =>
                      prod.precoUnitario.toLocaleString("pt-BR", {
                        style: "currency",
                        currency: "BRL",
                      })
                    )
                    .join(", ")}
                </TableCell>
                <TableCell>
                  {p.total.toLocaleString("pt-BR", {
                    style: "currency",
                    currency: "BRL",
                  })}
                </TableCell>
                <TableCell>
                  <Link href={`/Sistema/Proposta/Visualizar/${p.codigo}`}>
                    <LinkButton>Ver</LinkButton>
                  </Link>
                </TableCell>
              </TableRow>
            ))}
          </tbody>
        </Table>
      )}
    </Wrapper>
  );
}
