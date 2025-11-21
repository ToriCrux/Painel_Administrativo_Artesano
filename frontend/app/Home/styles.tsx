import tw from "tailwind-styled-components";

export const Container = tw.div`
  flex items-center justify-center min-h-screen bg-[#DFF4FF]
`;

export const Content = tw.div`
  flex flex-col items-center justify-center 
  bg-[#DFF4FF] 
  shadow-lg rounded-xl 
  p-10 w-[90%] max-w-md 
  border border-[#b5e8f2]
`;

export const LogoContainer = tw.div`
  flex justify-center mb-8
`;

export const ButtonGroup = tw.div`
  flex flex-col sm:flex-row sm:space-x-4 space-y-4 sm:space-y-0
`;

export const Button = tw.button`
  bg-[#36B0AC] text-white font-medium py-2 px-6 rounded-md 
  hover:bg-[#2e9b98] 
  hover:scale-105 
  transition-all duration-200 
  w-40 text-center
  cursor-pointer
  shadow-sm hover:shadow-md
`;
