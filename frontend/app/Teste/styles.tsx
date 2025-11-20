import tw from "tailwind-styled-components";

export const Page = tw.div`
  min-h-screen
  flex
  items-start
  justify-center
  bg-[#f2f6fa]
  py-12
`;

export const ButtonRow = tw.div`
  w-full
  max-w-5xl
  flex
  flex-col
  gap-6
  md:flex-row
  md:items-center
  md:justify-between
`;

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
  min-w-[220px]
`;
