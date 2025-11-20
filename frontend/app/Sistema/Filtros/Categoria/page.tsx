"use client";

import CategoriaMenu from "../Components/CategoriaMenu";
import { Wrapper, HeaderBar, Breadcrumb } from "../styles";
import { useRouter } from "next/navigation";

export default function CategoriaPage() {
  const router = useRouter();

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>FILTROS / CATEGORIA</Breadcrumb>
      </HeaderBar>

      <CategoriaMenu onBack={() => router.push("/Sistema/Filtros")} />
    </Wrapper>
  );
}
