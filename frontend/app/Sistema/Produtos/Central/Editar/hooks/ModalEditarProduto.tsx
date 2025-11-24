"use client";

import React, { useEffect, useState } from "react";
import Select, { MultiValue } from "react-select";
import { Produto } from "@/app/API/Produtos/apiServiceProduto";
import { getCores } from "@/app/API/Filtros/Cor/apiServiceCor";

interface CorOption {
  value: number;
  label: string;
}

interface ModalEditarProdutoProps {
  editando: Produto | null;
  setEditando: (produto: Produto | null) => void;
  handleSalvar: (produtoAtualizado: Produto) => void;
}

export default function ModalEditarProduto({
  editando,
  setEditando,
  handleSalvar,
}: ModalEditarProdutoProps) {
  const [cores, setCores] = useState<CorOption[]>([]);
  const [selectedCores, setSelectedCores] = useState<CorOption[]>([]);

  useEffect(() => {
    const carregarCores = async () => {
      try {
        const data = await getCores();
        const options: CorOption[] = data.map(
          (cor: { id: number; nome: string; hex: string }) => ({
            value: cor.id,
            label: `${cor.nome} (${cor.hex})`,
          })
        );
        setCores(options);

        if (editando) {
          const preSelecionadas = options.filter((opt) =>
            editando.corIds?.includes(opt.value)
          );
          setSelectedCores(preSelecionadas);
        }
      } catch (error) {
        console.error("Erro ao carregar cores:", error);
      }
    };

    carregarCores();
  }, [editando]);

  if (!editando) return null;

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setEditando({ ...editando, [name]: value });
  };

  const handleSalvarClick = () => {
    const atualizado: Produto = {
      ...editando,
      corIds: selectedCores.map((c) => c.value),
      categoriaNome: editando.categoria?.nome || "",
    };
    handleSalvar(atualizado);
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-lg w-[600px] shadow-xl">
        <h2 className="text-center text-[#36B0AC] text-lg font-semibold mb-4">
          Editar Produto
        </h2>

        <div className="grid grid-cols-2 gap-3">
          <input
            type="text"
            name="codigo"
            value={editando.codigo}
            onChange={handleChange}
            placeholder="Código"
            className="border p-2 rounded-md text-black"
          />
          <input
            type="text"
            name="nome"
            value={editando.nome}
            onChange={handleChange}
            placeholder="Nome"
            className="border p-2 rounded-md text-black"
          />
          <input
            type="text"
            name="categoriaNome"
            value={editando.categoria?.nome || ""}
            onChange={(e) =>
              setEditando({
                ...editando,
                categoria: { ...editando.categoria, nome: e.target.value },
              })
            }
            placeholder="Categoria"
            className="border p-2 rounded-md text-black col-span-2"
          />
          <Select
            isMulti
            options={cores}
            value={selectedCores}
            onChange={(opts: MultiValue<CorOption>) =>
              setSelectedCores(opts as CorOption[])
            }
            placeholder="Selecione as cores"
            className="col-span-2 text-black"
          />
          <input
            type="text"
            name="medidas"
            value={editando.medidas}
            onChange={handleChange}
            placeholder="Medidas"
            className="border p-2 rounded-md text-black col-span-2"
          />
          <input
            type="number"
            name="precoUnitario"
            value={editando.precoUnitario}
            onChange={handleChange}
            placeholder="Preço unitário"
            className="border p-2 rounded-md text-black col-span-2"
          />
          <textarea
            name="descricao"
            value={editando.descricao}
            onChange={handleChange}
            placeholder="Descrição"
            className="border p-2 rounded-md text-black col-span-2"
          />
        </div>

        <div className="flex justify-end mt-4 gap-4">
          <button
            onClick={() => setEditando(null)}
            className="bg-gray-300 hover:bg-gray-400 text-gray-700 px-4 py-2 rounded-md"
          >
            Cancelar
          </button>
          <button
            onClick={handleSalvarClick}
            className="bg-[#36B0AC] hover:bg-[#2c8e8a] text-white px-4 py-2 rounded-md"
          >
            Salvar
          </button>
        </div>
      </div>
    </div>
  );
}
