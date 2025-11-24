import tw from "tailwind-styled-components";

export const Wrapper = tw.div`
  w-full
  px-6
  mt-6
`;

export const Header = tw.div`
  mb-4
  flex
  justify-between
  items-center
`;

export const Title = tw.h2`
  text-teal-600
  font-semibold
  text-2xl
`;

export const Table = tw.table`
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

export const SearchInput = tw.input`
  border
  border-gray-300
  rounded-lg
  px-4
  py-2
  text-gray-700
  shadow-sm
  focus:outline-none
  focus:ring-2
  focus:ring-teal-500
  transition
  w-64
`;

export const SearchContainer = tw.div`
  w-full
  flex
  justify-start
  mt-3
  mb-4
`;

