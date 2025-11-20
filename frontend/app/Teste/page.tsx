"use client";

import { Page, ButtonRow, BigButton } from "./styles";

export default function TestePage() {
  return (
    <Page>
      <ButtonRow>
        <BigButton>Cadastrar Produto</BigButton>
        <BigButton>Editar Produto</BigButton>
        <BigButton>Deletar Produto</BigButton>
      </ButtonRow>
    </Page>
  );
}
