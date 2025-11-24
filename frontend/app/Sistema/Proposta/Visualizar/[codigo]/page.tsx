"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { getPropostaByCodigo, PropostaResponse } from "@/app/API/Proposta/propostaListApi";

import {
  InputGroup,
  Divider,
  Section,
  BigButton,
  Breadcrumb,
  ButtonRow,
  HeaderBar,
  Input,
  Label,
  Wrapper,
} from "../../Criar/styles";

import { useGerarPDF } from "../hooks/useGerarPDF";
import PdfTemplate from "../components/PdfTemplate";

export default function VisualizarPropostaDetalhePage() {
  const { codigo } = useParams<{ codigo: string }>();
  const router = useRouter();
  const [proposta, setProposta] = useState<PropostaResponse | null>(null);
  const { gerarPDF } = useGerarPDF();

  useEffect(() => {
    if (!codigo) return;
    const fetchProposta = async () => {
      try {
        const data = await getPropostaByCodigo(codigo);
        setProposta(data);
      } catch (error) {
        console.error("❌ Erro ao buscar proposta:", error);
      }
    };
    fetchProposta();
  }, [codigo]);

  if (!proposta || !proposta.cliente) {
    return (
      <Wrapper>
        <p>Carregando detalhes da proposta...</p>
      </Wrapper>
    );
  }

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>
          PROPOSTAS ORÇAMENTÁRIAS / VISUALIZAR PROPOSTA {proposta.codigo}
        </Breadcrumb>
      </HeaderBar>

      {/* INFORMAÇÕES DO CLIENTE */}
      <Section>
        <h3 className="text-[#3AAFA9]">INFORMAÇÕES DO CLIENTE</h3>
        <InputGroup>
          {Object.entries(proposta.cliente).map(([key, value]) => {
            const labels: Record<string, string> = {
              nome: "Nome",
              cpfCnpj: "CPF/CNPJ",
              telefone: "Telefone",
              email: "E-mail",
              cep: "CEP",
              endereco: "Endereço",
              bairro: "Bairro",
              cidade: "Cidade",
              uf: "UF",
              referencia: "Referência",
              complemento: "Complemento",
            };
            return (
              <div key={key}>
                <Label>{labels[key] ?? key}</Label>
                <Input value={String(value ?? "")} readOnly />
              </div>
            );
          })}
        </InputGroup>
      </Section>

      <Divider />

      {/* INFORMAÇÕES DA PROPOSTA */}
      <Section>
        <h3 className="text-[#3AAFA9]">INFORMAÇÕES DA PROPOSTA</h3>
        <InputGroup>
          <div>
            <Label>Código</Label>
            <Input value={proposta.codigo ?? ""} readOnly />
          </div>
          <div>
            <Label>Nome do Vendedor</Label>
            <Input value={proposta.nomeVendedor ?? ""} readOnly />
          </div>
          <div>
            <Label>Data da Proposta</Label>
            <Input value={proposta.dataProposta ?? ""} readOnly />
          </div>
          <div>
            <Label>Data de Validade</Label>
            <Input value={proposta.dataValidade ?? ""} readOnly />
          </div>
        </InputGroup>
      </Section>

      <Divider />

      {/* INFORMAÇÕES DOS PRODUTOS */}
      <Section>
        <h3 className="text-[#3AAFA9]">INFORMAÇÕES DOS PRODUTOS</h3>
        {proposta.produtos.map((produto, index) => (
          <InputGroup key={index}>
            <div>
              <Label>Código do Produto</Label>
              <Input value={produto.codigoProduto ?? ""} readOnly />
            </div>
            <div>
              <Label>Nome do Produto</Label>
              <Input value={produto.nomeProduto ?? ""} readOnly />
            </div>
            <div>
              <Label>Quantidade</Label>
              <Input value={String(produto.quantidade ?? 0)} readOnly />
            </div>
            <div>
              <Label>Preço Unitário</Label>
              <Input
                value={
                  produto.precoUnitario
                    ? produto.precoUnitario.toLocaleString("pt-BR", {
                        style: "currency",
                        currency: "BRL",
                      })
                    : "R$ 0,00"
                }
                readOnly
              />
            </div>
          </InputGroup>
        ))}

        <p className="mt-4 font-semibold text-lg">
          TOTAL:{" "}
          {proposta.total.toLocaleString("pt-BR", {
            style: "currency",
            currency: "BRL",
          })}
        </p>
      </Section>

      {/* BOTÕES */}
      <ButtonRow>
        <BigButton onClick={() => router.push("/Sistema/Proposta/Visualizar")}>
          Voltar para Lista
        </BigButton>

        {/* 🔹 Gera PDF a partir do template */}
        <BigButton
          onClick={() => gerarPDF("pdf-container", `Proposta_${proposta.codigo}`)}
        >
          Baixar PDF
        </BigButton>
      </ButtonRow>

      {/* 🔹 Template invisível mas renderizável (fora da tela) */}
      <div
        id="pdf-container"
        style={{
          position: "absolute",
          left: "-9999px",
          top: 0,
          visibility: "visible",
        }}
      >
        <PdfTemplate proposta={proposta} />
      </div>
    </Wrapper>
  );
}
