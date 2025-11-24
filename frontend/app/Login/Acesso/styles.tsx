import tw from "tailwind-styled-components";

export const Container = tw.div`
  flex items-center justify-center min-h-screen bg-[#DFF4FF]
`;

export const Content = tw.div`
  flex flex-col items-center justify-center 
  bg-[#DFF4FF] 
  shadow-lg rounded-xl 
  p-10 w-[95%] max-w-3xl 
  border border-[#b5e8f2]
`;

export const LogoContainer = tw.div`
  flex justify-center mb-8
`;

export const Form = tw.form`
  flex flex-col sm:flex-row sm:items-end sm:space-x-4 w-full justify-center
`;

export const InputGroup = tw.div`
  flex flex-col mb-4 sm:mb-0
`;

export const Label = tw.label`
  text-gray-700 font-medium mb-1
`;

export const Input = tw.input`
  border border-gray-300 rounded-md px-4 py-2 
  focus:outline-none focus:ring-2 focus:ring-[#36B0AC] 
  w-64 placeholder:text-gray-400
  text-black
`;

export const Button = tw.button`
  bg-[#36B0AC] text-white font-medium py-2 px-8 rounded-md 
  hover:bg-[#2e9b98] hover:scale-105 transition-all duration-200 
  cursor-pointer shadow-sm hover:shadow-md disabled:opacity-60 disabled:cursor-not-allowed
`;

export const ErrorText = tw.p`
  text-red-500 text-sm mt-4
`;
