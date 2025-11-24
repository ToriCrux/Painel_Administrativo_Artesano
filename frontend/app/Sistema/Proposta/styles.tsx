import tw from "tailwind-styled-components";

export const Wrapper = tw.div`
  w-full
  px-6
  mt-6
`;

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
`;

export const ButtonRow = tw.div`
  w-full
  max-w-5xl
  mx-auto
  flex
  flex-col
  gap-6
  md:flex-row
  md:justify-between
  md:items-center
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
  min-w-[260px]
`;
