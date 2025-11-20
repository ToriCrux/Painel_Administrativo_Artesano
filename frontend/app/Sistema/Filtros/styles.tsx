import tw from "tailwind-styled-components";

/* === Estrutura principal da página === */
export const Wrapper = tw.div`
  flex flex-col
  w-full
  px-6
  mt-6
`;

/* === Cabeçalho e título === */
export const HeaderBar = tw.div`
  flex
  items-center
  justify-between
  mb-6
`;

export const Breadcrumb = tw.h2`
  text-teal-600
  font-semibold
  tracking-wide
  text-lg
`;

/* === Container dos botões principais ou submenu === */
export const ButtonRow = tw.div`
  w-full
  max-w-4xl
  mx-auto
  flex
  flex-col
  gap-6
  md:flex-row
  md:justify-between
  md:items-center
  mt-8
`;

/* === Botões grandes (Categoria / Cores / Submenu) === */
export const BigButton = tw.button`
  bg-teal-500
  text-white
  text-lg
  font-semibold
  px-8
  py-4
  rounded-lg
  shadow
  hover:opacity-90
  focus:outline-none
  focus:ring-4
  focus:ring-teal-300
  active:scale-[0.98]
  transition
  flex-1
  cursor-pointer
  min-w-[220px]
`;

/* === Botão de voltar (submenu Categoria) === */
export const BackButton = tw.button`
  mt-10
  px-6
  py-2
  bg-gray-200
  text-gray-700
  rounded-md
  font-medium
  hover:bg-gray-300
  transition
  duration-200
  mx-auto
  block
`;
