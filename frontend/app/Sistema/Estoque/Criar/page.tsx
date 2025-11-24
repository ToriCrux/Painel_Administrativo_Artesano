"use client";

import { useEffect, useState } from "react";
import {
  getProdutos,

  criarEstoqueZerado,
} from "@/app/API/Estoque/criarEstoque.api";
import {
  Wrapper,
  Header,
  Title,
  FormContainer,
  Label,
  Select,
  Button,
  Message,
} from "./styles";
import { getEstoque } from "@/app/API/Estoque/estoque.api";

interface Produto {
  id: number;
  nome: string;
  codigo: string;
}

export default function CriarEstoquePage() {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [produtoSelecionado, setProdutoSelecionado] = useState<string>("");
  const [mensagem, setMensagem] = useState<string | null>(null);
  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState<boolean>(false);

  useEffect(() => {
    async function fetchData() {
      try {
        const produtosData = await getProdutos();
        const estoqueData = await getEstoque();

        const idsComEstoque = new Set(
          (estoqueData.content || []).map((e: any) => e.produtoId)
        );

        const produtosSemEstoque = (produtosData.content || []).filter(
          (p: any) => !idsComEstoque.has(p.id)
        );

        setProdutos(produtosSemEstoque);
      } catch {
        setErro("Erro ao carregar produtos.");
      }
    }
    fetchData();
  }, []);

  const criar = async () => {
    setErro(null);
    setMensagem(null);

    if (!produtoSelecionado) {
      setErro("Selecione um produto.");
      return;
    }

    try {
      setCarregando(true);
      const data = await criarEstoqueZerado(Number(produtoSelecionado));
      setMensagem(`✅ Estoque criado para ${data.produtoNome}`);
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  };

  return (
    <Wrapper>
      <Header>
        <Title>Criar Estoque Manual</Title>
      </Header>

      <FormContainer>
        <Label>Selecione um produto:</Label>

        <Select
          value={produtoSelecionado}
          onChange={(e) => setProdutoSelecionado(e.target.value)}
        >
          <option value="">-- Selecione --</option>
          {produtos.map((p) => (
            <option key={p.id} value={p.id}>
              {p.nome} — ({p.codigo})
            </option>
          ))}
        </Select>

        <Button onClick={criar} disabled={carregando}>
          {carregando ? "Criando..." : "Criar Estoque Zerado"}
        </Button>

        {mensagem && <Message success>{mensagem}</Message>}
        {erro && <Message>{erro}</Message>}
      </FormContainer>
    </Wrapper>
  );
}
