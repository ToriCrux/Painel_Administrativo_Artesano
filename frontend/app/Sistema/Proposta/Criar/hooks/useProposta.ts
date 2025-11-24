"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { criarProposta } from "@/app/API/Proposta/propostaApi";
import { getProdutos, Produto as ProdutoSistema } from "@/app/API/Produtos/apiServiceProduto";



export interface Cliente {
  nome: string;
  cpfCnpj: string;
  telefone: string;
  email: string;
  cep: string;
  endereco: string;
  bairro: string;
  cidade: string;
  uf: string;
  referencia: string;
  complemento: string;
}

export interface Produto {
  codigoProduto: string;
  nomeProduto: string;
  quantidade: number;
  precoUnitario: number;
}

export interface Proposta {
  codigo: string;
  nomeVendedor: string;
  dataProposta: string;
  dataValidade: string;
  cliente: Cliente;
  produtos: Produto[];
}

export const useProposta = () => {
  const router = useRouter();

  const [form, setForm] = useState<Proposta>({
    codigo: "",
    nomeVendedor: "",
    dataProposta: "",
    dataValidade: "",
    cliente: {
      nome: "",
      cpfCnpj: "",
      telefone: "",
      email: "",
      cep: "",
      endereco: "",
      bairro: "",
      cidade: "",
      uf: "",
      referencia: "",
      complemento: "",
    },
    produtos: [
      { codigoProduto: "", nomeProduto: "", quantidade: 1, precoUnitario: 0 },
    ],
  });

  // 🔹 Estado para armazenar produtos disponíveis no sistema
  const [produtosDisponiveis, setProdutosDisponiveis] = useState<ProdutoSistema[]>([]);

  // 🔹 Carrega produtos cadastrados ao iniciar
  useEffect(() => {
    const fetchProdutos = async () => {
      try {
        const produtos = await getProdutos();
        setProdutosDisponiveis(produtos);
      } catch (error) {
        console.error("Erro ao carregar produtos:", error);
      }
    };
    fetchProdutos();
  }, []);

  // 🔹 Atualiza campos do cliente e proposta
  const handleChange = (
    section: "cliente" | "",
    field: string,
    value: string
  ) => {
    if (section === "cliente") {
      setForm((prev) => ({
        ...prev,
        cliente: { ...prev.cliente, [field]: value },
      }));
    } else {
      setForm((prev) => ({ ...prev, [field]: value }));
    }
  };

  // 🔹 Atualiza produto selecionado ou campos individuais
  const handleProdutoChange = (index: number, field: string, value: string) => {
    const updatedProdutos = [...form.produtos];
    const produtoAtual = updatedProdutos[index];

    // Se o usuário selecionar pelo código
    if (field === "codigoProduto") {
      const produtoSelecionado = produtosDisponiveis.find(
        (p) => p.codigo === value
      );
      if (produtoSelecionado) {
        produtoAtual.codigoProduto = produtoSelecionado.codigo;
        produtoAtual.nomeProduto = produtoSelecionado.nome;
        produtoAtual.precoUnitario = produtoSelecionado.precoUnitario;
      }
    }

    // Se o usuário selecionar pelo nome
    if (field === "nomeProduto") {
      const produtoSelecionado = produtosDisponiveis.find(
        (p) => p.nome === value
      );
      if (produtoSelecionado) {
        produtoAtual.codigoProduto = produtoSelecionado.codigo;
        produtoAtual.nomeProduto = produtoSelecionado.nome;
        produtoAtual.precoUnitario = produtoSelecionado.precoUnitario;
      }
    }

    // Atualiza manualmente quantidade ou preço
    if (field === "quantidade" || field === "precoUnitario") {
      produtoAtual[field] = Number(value);
    }

    updatedProdutos[index] = produtoAtual;
    setForm({ ...form, produtos: updatedProdutos });
  };

  // 🔹 Adiciona novo produto à lista
  const adicionarProduto = () => {
    setForm({
      ...form,
      produtos: [
        ...form.produtos,
        { codigoProduto: "", nomeProduto: "", quantidade: 1, precoUnitario: 0 },
      ],
    });
  };

  // 🔹 Calcula total
  const calcularTotal = () =>
    form.produtos.reduce(
      (acc, p) => acc + Number(p.quantidade) * Number(p.precoUnitario),
      0
    );

  // 🔹 Envia proposta ao backend
  const handleSubmit = async (): Promise<void> => {
    try {
      await criarProposta(form);
      alert("Proposta criada com sucesso!");
      router.push("/Proposta/Visualizar");
    } catch (error: unknown) {
      if (error instanceof Error) {
        alert(`Erro ao criar proposta: ${error.message}`);
      } else {
        alert("Erro desconhecido ao criar proposta.");
      }
    }
  };

  return {
    form,
    produtosDisponiveis,
    handleChange,
    handleProdutoChange,
    adicionarProduto,
    calcularTotal,
    handleSubmit,
  };
};
