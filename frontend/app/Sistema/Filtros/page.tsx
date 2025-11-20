"use client";

import { useRouter } from "next/navigation";
import { Wrapper, HeaderBar, Breadcrumb, ButtonRow, BigButton } from "./styles";
import { useState } from "react";

type FilterMenu = "main";

export default function FiltrosPage() {
  const router = useRouter();
  const [currentMenu] = useState<FilterMenu>("main");

  const handleCategoriaClick = () => {
    router.push("/Sistema/Filtros/Categoria");
  };

  const handleCoresClick = () => {
    router.push("/Sistema/Filtros/Cores");
  };

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>FILTROS</Breadcrumb>
      </HeaderBar>

      {currentMenu === "main" && (
        <ButtonRow>
          <BigButton onClick={handleCategoriaClick}>Categoria</BigButton>
          <BigButton onClick={handleCoresClick}>Cores</BigButton>
        </ButtonRow>
      )}
    </Wrapper>
  );
}
