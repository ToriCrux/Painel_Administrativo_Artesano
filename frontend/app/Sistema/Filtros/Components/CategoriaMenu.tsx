"use client";

import { useRouter } from "next/navigation";
import { ButtonRow, BigButton } from "../styles";

type CategoriaMenuProps = {
  onBack: () => void;
};

export default function CategoriaMenu({ onBack }: CategoriaMenuProps) {
  const router = useRouter();

  const handleNavigate = (path: string) => {
    router.push(path);
  };

  return (
    <>
      <ButtonRow>
        <BigButton onClick={() => handleNavigate("/Sistema/Filtros/Categoria/adicionar")}>
          Adicionar Categoria
        </BigButton>
        <BigButton onClick={() => handleNavigate("/Sistema/Filtros/Categoria/verificar")}>
          Verificar Categorias
        </BigButton>
        <BigButton onClick={() => handleNavigate("/Sistema/Filtros/Categoria/excluir")}>
          Excluir Categorias
        </BigButton>
      </ButtonRow>

      <div className="flex justify-center mt-8">
        <button
          onClick={onBack}
          className="px-6 py-2 rounded-md bg-gray-200 text-gray-700 font-medium hover:bg-gray-300 transition"
        >
          ← Voltar
        </button>
      </div>
    </>
  );
}
