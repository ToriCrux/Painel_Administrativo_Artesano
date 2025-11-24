"use client";

import { useRouter } from "next/navigation";
import { Wrapper, HeaderBar, Breadcrumb, ButtonRow, BigButton } from "./styles";

export default function PropostasPage() {
  const router = useRouter();

  const handleNovaProposta = () => {
    router.push("/Sistema/Proposta/Criar");
  };

  const handleVisualizarPropostas = () => {
    router.push("/Sistema/Proposta/Visualizar");
  };

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>PROPOSTAS ORÇAMENTÁRIAS</Breadcrumb>
      </HeaderBar>

      <ButtonRow>
        <BigButton onClick={handleNovaProposta}>
          Criar Nova Proposta Orçamentária
        </BigButton>
        <BigButton onClick={handleVisualizarPropostas}>
          Visualizar Propostas Orçamentárias
        </BigButton>
      </ButtonRow>
    </Wrapper>
  );
}
