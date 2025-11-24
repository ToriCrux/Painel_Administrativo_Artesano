"use client";

import { PropostaResponse } from "@/app/API/Proposta/propostaListApi";
import "../styles/pdf.css";

interface PdfTemplateProps {
  proposta: PropostaResponse;
}

export default function PdfTemplate({ proposta }: PdfTemplateProps) {
  // 🔹 Mapeamento de rótulos amigáveis
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
    <div id="pdf-content" className="pdf-wrapper">
      <header className="pdf-header">
        <h1 className="pdf-title">Proposta Comercial</h1>
        <h2 className="pdf-subtitle">Artesano Brasil</h2>
        <div className="pdf-divider"></div>
      </header>

      {/* ======================= INFORMAÇÕES DO CLIENTE ======================= */}
      <section>
        <h3>Informações do Cliente</h3>
        <table className="pdf-table">
          <tbody>
            {Object.entries(proposta.cliente).map(([key, value]) => (
              <tr key={key}>
                <td className="pdf-label">{labels[key] ?? key}</td>
                <td className="pdf-value">{String(value ?? "")}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>

      {/* ======================= INFORMAÇÕES DOS PRODUTOS ======================= */}
      <section>
        <h3>Produtos</h3>
        <table className="pdf-table">
          <thead>
            <tr>
              <th>Código</th>
              <th>Produto</th>
              <th>Qtd</th>
              <th>Preço Unitário</th>
            </tr>
          </thead>
          <tbody>
            {proposta.produtos.map((p, i) => (
              <tr key={i}>
                <td>{p.codigoProduto}</td>
                <td>{p.nomeProduto}</td>
                <td>{p.quantidade}</td>
                <td>
                  {p.precoUnitario.toLocaleString("pt-BR", {
                    style: "currency",
                    currency: "BRL",
                  })}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <p className="pdf-total">
          Total:{" "}
          {proposta.total.toLocaleString("pt-BR", {
            style: "currency",
            currency: "BRL",
          })}
        </p>
      </section>
    </div>
  );
}
