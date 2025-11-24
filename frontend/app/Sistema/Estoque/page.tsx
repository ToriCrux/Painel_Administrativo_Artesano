"use client";

import { Wrapper, HeaderBar, Breadcrumb, ButtonRow, BigButton } from "./styles";
import Link from "next/link";

export default function EstoquePage() {
  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>ESTOQUE</Breadcrumb>
      </HeaderBar>

      <ButtonRow>
        <Link href="/Sistema/Estoque/Atualizar">
          <BigButton>Atualizar Estoque</BigButton>
        </Link>
        <Link href="/Sistema/Estoque/Visualizar">
          <BigButton>Visualizar Estoque</BigButton>
        </Link>
        <Link href="/Sistema/Estoque/Movimentacoes">
          <BigButton>Movimentações de Estoque</BigButton>
        </Link>
        <Link href="/Sistema/Estoque/Criar">
          <BigButton>Criar Estoque Manual</BigButton>
        </Link>
      </ButtonRow>
    </Wrapper>
  );
}
