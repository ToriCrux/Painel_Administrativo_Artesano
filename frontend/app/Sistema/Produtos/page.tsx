"use client";

import { useRouter } from "next/navigation";
import { Wrapper, HeaderBar, Breadcrumb, ButtonRow, BigButton } from "./styles";

export default function ProdutosPage() {
  const router = useRouter();

  const handleCentralProdutos = () => {
    router.push("/Sistema/Produtos/Central");
  };

  const handleVisualizarProdutos = () => {
    router.push("/Sistema/Produtos/Visualizar");
  };

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>PRODUTOS</Breadcrumb>
      </HeaderBar>

      <ButtonRow>
        <BigButton onClick={handleCentralProdutos}>Central de Produtos</BigButton>
        <BigButton onClick={handleVisualizarProdutos}>Visualizar Produtos</BigButton>
      </ButtonRow>
    </Wrapper>
  );
}
