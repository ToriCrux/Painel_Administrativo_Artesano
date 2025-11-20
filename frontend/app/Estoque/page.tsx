"use client";

import { Wrapper, HeaderBar, Breadcrumb, ButtonRow, BigButton } from "./styles";

export default function EstoquePage() {
  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>ESTOQUE</Breadcrumb>
        {/* opcional: botão/ícone de voltar aqui */}
      </HeaderBar>

      <ButtonRow>
        <BigButton>Atualizar Estoque</BigButton>
        <BigButton>Visualizar Estoque</BigButton>
        <BigButton>Movimentações de Estoque</BigButton>
      </ButtonRow>
    </Wrapper>
  );
}
