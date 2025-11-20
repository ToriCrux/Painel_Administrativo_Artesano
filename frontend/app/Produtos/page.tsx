"use client";

import { Wrapper, HeaderBar, Breadcrumb, ButtonRow, BigButton } from "./styles";

export default function ProdutosPage() {
  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>PRODUTOS</Breadcrumb>

        {/* Ícone de voltar opcional – deixe como quiser */}
        {/* <button aria-label="Voltar" className="text-teal-600 hover:opacity-80">
          ⟲
        </button> */}
      </HeaderBar>

      <ButtonRow>
        <BigButton>Central de Produtos</BigButton>
        <BigButton>Visualizar Produtos</BigButton>
        <BigButton>Categorização de Produtos</BigButton>
      </ButtonRow>
    </Wrapper>
  );
}
