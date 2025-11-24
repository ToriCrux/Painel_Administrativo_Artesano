"use client";

import { useProposta } from "./hooks/useProposta";
import {
  Wrapper,
  HeaderBar,
  Breadcrumb,
  Section,
  InputGroup,
  Input,
  Label,
  ButtonRow,
  BigButton,
  Divider,
} from "./styles";

export default function CriarPropostaPage() {
  const {
    form,
    produtosDisponiveis,
    handleChange,
    handleProdutoChange,
    adicionarProduto,
    calcularTotal,
    handleSubmit,
  } = useProposta();

  return (
    <Wrapper>
        <HeaderBar>
            <Breadcrumb>PROPOSTAS ORÇAMENTÁRIAS / CRIAR NOVA PROPOSTA</Breadcrumb>
        </HeaderBar>

        {/* ==================== CLIENTE ==================== */}
        <Section>
        <h3>INFORMAÇÕES DO CLIENTE</h3>
        <InputGroup>
            {Object.keys(form.cliente).map((field) => {
            const key = field as keyof typeof form.cliente;

            // 🔹 Mapa de labels formatados
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
                <Input
                    value={form.cliente[key]}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                    handleChange("cliente", key, e.target.value)
                    }
                />
                </div>
            );
            })}
        </InputGroup>
        </Section>

        <Divider />

        {/* ==================== PROPOSTA ==================== */}
        <Section>
            <h3 className="text-[#3AAFA9]">INFORMAÇÕES DA PROPOSTA</h3>
            <InputGroup>
            <div>
                <Label>Código da Proposta</Label>
                <Input
                value={form.codigo}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                    handleChange("", "codigo", e.target.value)
                }
                />
            </div>
            <div>
                <Label>Nome do Vendedor</Label>
                <Input
                value={form.nomeVendedor}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                    handleChange("", "nomeVendedor", e.target.value)
                }
                />
            </div>
            <div>
                <Label>Data da Proposta</Label>
                <Input
                type="date"
                value={form.dataProposta}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                    handleChange("", "dataProposta", e.target.value)
                }
                />
            </div>
            <div>
                <Label>Data de Validade</Label>
                <Input
                type="date"
                value={form.dataValidade}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                    handleChange("", "dataValidade", e.target.value)
                }
                />
            </div>
            </InputGroup>
        </Section>

        <Divider />

        {/* ==================== PRODUTOS ==================== */}
        <Section>
            <h3 className="text-[#3AAFA9]">INFORMAÇÕES DO PRODUTO</h3>

            {form.produtos.map((produto, i) => (
            <InputGroup key={i}>
                {/* Código do Produto */}
                <div>
                <Label>Código do Produto</Label>
                <select
                    className="border border-gray-300 rounded-md p-2 w-full focus:ring-2 focus:ring-teal-400"
                    value={produto.codigoProduto}
                    onChange={(e) =>
                    handleProdutoChange(i, "codigoProduto", e.target.value)
                    }
                >
                    <option value="">Selecione</option>
                    {produtosDisponiveis.map((p) => (
                    <option key={p.codigo} value={p.codigo}>
                        {p.codigo}
                    </option>
                    ))}
                </select>
                </div>

                {/* Nome do Produto */}
                <div>
                <Label>Nome do Produto</Label>
                <select
                    className="border border-gray-300 rounded-md p-2 w-full focus:ring-2 focus:ring-teal-400"
                    value={produto.nomeProduto}
                    onChange={(e) =>
                    handleProdutoChange(i, "nomeProduto", e.target.value)
                    }
                >
                    <option value="">Selecione</option>
                    {produtosDisponiveis.map((p) => (
                    <option key={p.codigo} value={p.nome}>
                        {p.nome}
                    </option>
                    ))}
                </select>
                </div>

                {/* Quantidade */}
                <div>
                <Label>Quantidade</Label>
                <Input
                    type="number"
                    min="1"
                    value={produto.quantidade}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                    handleProdutoChange(i, "quantidade", e.target.value)
                    }
                />
                </div>

                {/* Preço Unitário */}
                <div>
                <Label>Preço Unitário</Label>
                <Input
                    type="text"
                    inputMode="decimal"
                    placeholder="Digite o preço (ex: 25.50)"
                    value={produto.precoUnitario}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    // Permite números, ponto e vírgula
                    const value = e.target.value.replace(",", ".");
                    handleProdutoChange(i, "precoUnitario", value);
                    }}
                />
                </div>


            </InputGroup>
            ))}

            <p className="mt-4 font-semibold text-lg">
            TOTAL:{" "}
            {calcularTotal().toLocaleString("pt-BR", {
                style: "currency",
                currency: "BRL",
            })}
            </p>
        </Section>

        {/* ==================== BOTÕES ==================== */}
        <ButtonRow>
            <BigButton onClick={adicionarProduto}>
            Adicionar outro Produto
            </BigButton>
            <BigButton onClick={handleSubmit}>Finalizar Orçamento</BigButton>
        </ButtonRow>
    </Wrapper>
  );
}
