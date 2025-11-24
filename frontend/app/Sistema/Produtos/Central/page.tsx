"use client";

import { useRouter } from "next/navigation";
import { Wrapper, HeaderBar, Breadcrumb, ButtonRow, BigButton } from "../styles";

export default function VisualizarProdutosPage() {
  const router = useRouter();

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>Produtos / Central Produtos</Breadcrumb>
      </HeaderBar>

      <ButtonRow>
        <BigButton onClick={() => router.push("/Sistema/Produtos/Central/Cadastrar")}>
          Cadastrar Produto
        </BigButton>
        <BigButton onClick={() => router.push("/Sistema/Produtos/Central/Editar")}>
          Editar Produto
        </BigButton>
      </ButtonRow>

      <div className="flex justify-center mt-8">
        <button
          onClick={() => router.push("/Sistema/Produtos")}
          className="px-6 py-2 rounded-md bg-gray-200 text-gray-700 font-medium hover:bg-gray-300 transition"
        >
          ← Voltar
        </button>
      </div>
    </Wrapper>
  );
}
