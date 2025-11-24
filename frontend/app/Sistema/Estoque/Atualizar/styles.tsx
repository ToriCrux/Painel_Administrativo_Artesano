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
  mt-3
  mb-4
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
  w-80
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

export const EditIcon = tw.button`
  text-teal-600
  hover:text-teal-800
  text-xl
  cursor-pointer
  transition
`;

export const ModalOverlay = tw.div`
  fixed
  inset-0
  backdrop-blur-sm
  bg-white/40
  flex
  items-center
  justify-center
  z-50
`;


export const ModalContent = tw.div`
  bg-white
  rounded-xl
  p-6
  shadow-lg
  w-[400px]
`;

export const ModalInput = tw.input`
  border
  border-gray-300
  rounded-lg
  px-4
  py-2
  w-full
  text-gray-700
  focus:ring-2
  focus:ring-teal-500
  mt-4
`;

export const ModalButtonRow = tw.div`
  flex
  justify-end
  gap-3
  mt-6
`;

export const ModalButton = tw.button`
  px-4
  py-2
  rounded-lg
  font-medium
  transition
  text-white
  bg-teal-600
  hover:bg-teal-700
  active:scale-95
`;

export const DeleteIcon = tw.button`
  text-red-600
  hover:text-red-800
  text-xl
  cursor-pointer
  transition
`;

