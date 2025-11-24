import tw from "tailwind-styled-components";

export const Wrapper = tw.div`
  w-full
  px-6
  mt-6
`;

export const Header = tw.div`
  mb-4
`;

export const Title = tw.h2`
  text-teal-600
  font-semibold
  text-2xl
`;

export const SearchContainer = tw.div`
  relative
  w-[400px]
`;

export const SearchInput = tw.input`
  border
  border-gray-300
  rounded-lg
  px-4
  py-2
  w-full
  text-gray-700
  shadow-sm
  focus:ring-2
  focus:ring-teal-500
  focus:outline-none
  transition
`;

export const Dropdown = tw.div`
  absolute
  top-12
  left-0
  w-full
  bg-white
  border
  border-gray-300
  rounded-md
  shadow-md
  max-h-60
  overflow-y-auto
  z-50
`;

export const DropdownItem = tw.div`
  px-4
  py-2
  hover:bg-teal-100
  cursor-pointer
  text-gray-700
  transition
`;

export const NoResults = tw.div`
  px-4
  py-2
  text-gray-500
  text-sm
`;

export const MovimentacaoTable = tw.table`
  w-full
  border-collapse
  mt-4
  shadow-md
  rounded-lg
  overflow-hidden
`;

export const Th = tw.th`
  bg-teal-600
  text-white
  font-semibold
  py-3
  px-4
  text-left
`;

export const Td = tw.td`
  border-b
  border-gray-200
  py-3
  px-4
  text-gray-700
`;

export const SubTitle = tw.h3`
  text-teal-700
  font-medium
  text-lg
`;

export const BackButton = tw.button`
  bg-gray-300
  text-gray-700
  px-3
  py-1
  rounded-md
  hover:bg-gray-400
  transition
`;
