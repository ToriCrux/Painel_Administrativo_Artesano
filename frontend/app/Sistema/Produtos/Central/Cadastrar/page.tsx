"use client";

import { JSX, useState } from "react";
import Select, { MultiValue } from "react-select";
import { Wrapper, HeaderBar, Breadcrumb } from "../../styles";
import { useRouter } from "next/navigation";
import { useCadastrarProduto } from "./hooks/useCadastrarProduto";

interface CorOption {
  value: number;
  label: JSX.Element;
}

export default function CadastrarProdutoPage() {
  const router = useRouter();
  const {
    cadastrarProduto,
    loading,
    success,
    error,
    setSuccess,
    categorias,
    cores,
  } = useCadastrarProduto();

  const [formData, setFormData] = useState({
    codigo: "",
    nome: "",
    categoriaNome: "",
    corIds: [] as number[],
    medidas: "",
    precoUnitario: 0,
    ativo: true,
    descricao: "",
  });

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSelectCores = (selectedOptions: MultiValue<CorOption>) => {
    const ids = selectedOptions.map((opt) => opt.value);
    setFormData((prev) => ({ ...prev, corIds: ids }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await cadastrarProduto(formData);
  };

  const corOptions: CorOption[] = cores.map((cor) => ({
    value: cor.id,
    label: (
      <div className="flex items-center gap-2">
        <span
          className="inline-block w-4 h-4 rounded-full border"
          style={{ backgroundColor: cor.hex }}
        ></span>
        {`${cor.nome} (${cor.hex.toUpperCase()})`}
      </div>
    ),
  }));

  return (
    <Wrapper>
      <HeaderBar>
        <Breadcrumb>
          PRODUTOS / CENTRAL DE PRODUTOS / CADASTRAR PRODUTO
        </Breadcrumb>
      </HeaderBar>

      <form
        onSubmit={handleSubmit}
        className="grid grid-cols-1 sm:grid-cols-3 gap-6 max-w-5xl mx-auto mt-8"
      >
        <input
          name="codigo"
          value={formData.codigo}
          onChange={handleChange}
          placeholder="Código do produto..."
          className="border border-gray-300 rounded-md p-2 w-full text-black"
        />

        <input
          name="nome"
          value={formData.nome}
          onChange={handleChange}
          placeholder="Nome do produto..."
          className="border border-gray-300 rounded-md p-2 w-full text-black"
        />

        <select
          name="categoriaNome"
          value={formData.categoriaNome}
          onChange={handleChange}
          className="border border-gray-300 rounded-md p-2 w-full text-black"
        >
          <option value="">Selecione uma categoria</option>
          {categorias.map((cat) => (
            <option key={cat.id} value={cat.nome}>
              {cat.nome}
            </option>
          ))}
        </select>

        <input
          name="medidas"
          value={formData.medidas}
          onChange={handleChange}
          placeholder="Medidas do produto..."
          className="border border-gray-300 rounded-md p-2 w-full text-black"
        />

        <input
          type="number"
          name="precoUnitario"
          value={formData.precoUnitario}
          onChange={handleChange}
          placeholder="Preço por unidade..."
          className="border border-gray-300 rounded-md p-2 w-full text-black"
        />

        <div className="sm:col-span-1">
          <Select
            isMulti
            options={corOptions}
            onChange={handleSelectCores}
            placeholder="Selecione as cores..."
            classNamePrefix="react-select"
            styles={{
              control: (base) => ({
                ...base,
                borderColor: "#ccc",
                borderRadius: "0.375rem",
                minHeight: "38px",
              }),
              option: (base, state) => ({
                ...base,
                backgroundColor: state.isFocused ? "#e6f7f7" : "white",
                color: "#000",
              }),
              multiValue: (base) => ({
                ...base,
                backgroundColor: "#36B0AC33",
                borderRadius: "0.375rem",
              }),
            }}
          />
        </div>

        <textarea
          name="descricao"
          value={formData.descricao}
          onChange={(e) =>
            setFormData((prev) => ({ ...prev, descricao: e.target.value }))
          }
          placeholder="Descrição do produto..."
          className="border border-gray-300 rounded-md p-2 w-full text-black col-span-full"
        />

        <div className="col-span-full flex justify-center gap-6 mt-6">
          <button
            type="submit"
            disabled={loading}
            className="bg-green-600 hover:bg-green-700 text-white font-semibold py-2 px-6 rounded-md transition disabled:opacity-50"
          >
            {loading ? "Cadastrando..." : "Cadastrar Produto"}
          </button>
          <button
            type="button"
            onClick={() => router.push("/Sistema/Produtos/Central")}
            className="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-6 rounded-md transition"
          >
            Cancelar
          </button>
        </div>
      </form>

      {success && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-8 shadow-lg text-center">
            <div className="text-[#36B0AC] text-4xl mb-4">✔</div>
            <p className="text-[#36B0AC] text-lg font-semibold mb-4">
              PRODUTO CADASTRADO COM SUCESSO!
            </p>
            <button
              onClick={() => setSuccess(false)}
              className="bg-[#36B0AC] text-white py-2 px-6 rounded-md hover:opacity-90"
            >
              OK
            </button>
          </div>
        </div>
      )}

      {error && (
        <p className="text-red-600 text-center mt-4 font-medium">Erro: {error}</p>
      )}
    </Wrapper>
  );
}
