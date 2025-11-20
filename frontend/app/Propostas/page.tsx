"use client";

import { Wrapper, HeaderBar, Breadcrumb, ButtonRow, BigButton } from "./styles";

export default function PropostasPage() {
  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>PROPOSTAS ORÇAMENTÁRIAS</Breadcrumb>
        {/* opcional: ícone/botão de voltar */}
      </HeaderBar>

      <ButtonRow>
        <BigButton>Criar Nova Proposta Orçamentária</BigButton>
        <BigButton>Visualizar Propostas Orçamentárias</BigButton>
      </ButtonRow>
    </Wrapper>
  );
}
