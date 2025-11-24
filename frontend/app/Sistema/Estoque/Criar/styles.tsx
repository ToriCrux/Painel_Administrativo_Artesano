import tw from "tailwind-styled-components";

export const Wrapper = tw.div`
  w-full
  px-6
  mt-6
  flex
  flex-col
  items-center
`;

export const Header = tw.div`
  w-full
  flex
  justify-center
  mb-6
`;

export const Title = tw.h2`
  text-2xl
  font-bold
  text-teal-700
`;

export const FormContainer = tw.div`
  bg-white
  shadow-md
  rounded-xl
  p-6
  w-full
  max-w-lg
  flex
  flex-col
  gap-4
`;

export const Label = tw.label`
  font-medium
  text-gray-700
`;

export const Select = tw.select`
  w-full
  p-2
  border
  border-gray-300
  rounded-md
  focus:outline-none
  focus:ring-2
  focus:ring-teal-500
  text-black
  bg-white
`;

export const Button = tw.button`
  w-full
  py-2
  bg-teal-600
  text-white
  font-semibold
  rounded-md
  hover:bg-teal-700
  transition
  disabled:bg-gray-400
  disabled:cursor-not-allowed
`;

export const Message = tw.p<{ success?: boolean }>`
  text-center
  font-medium
  ${(p) => (p.success ? "text-green-600" : "text-red-600")}
`;
