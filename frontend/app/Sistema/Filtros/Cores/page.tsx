"use client";

import { useRouter } from "next/navigation";
import { Wrapper, HeaderBar, Breadcrumb, ButtonRow, BigButton } from "../styles";

export default function CoresPage() {
  const router = useRouter();

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>FILTROS / CORES</Breadcrumb>
      </HeaderBar>

      <ButtonRow>
        <BigButton onClick={() => router.push("/Sistema/Filtros/Cores/Adicionar")}>
          Adicionar Cores
        </BigButton>
        <BigButton onClick={() => router.push("/Sistema/Filtros/Cores/Verificar")}>
          Verificar Cores
        </BigButton>
        <BigButton onClick={() => router.push("/Sistema/Filtros/Cores/Excluir")}>
          Excluir Cores
        </BigButton>
      </ButtonRow>

      <div className="flex justify-center mt-8">
        <button
          onClick={() => router.push("/Sistema/Filtros")}
          className="px-6 py-2 rounded-md bg-gray-200 text-gray-700 font-medium hover:bg-gray-300 transition"
        >
          ← Voltar
        </button>
      </div>
    </Wrapper>
  );
}
