import tw from "tailwind-styled-components";

export const Wrapper = tw.div`
  w-full px-6 mt-6 text-black
`;

export const HeaderBar = tw.div`
  flex items-center justify-between mb-6
`;

export const Breadcrumb = tw.h2`
  text-teal-600 font-semibold tracking-wide
`;

export const Section = tw.section`
  mb-10
`;

export const InputGroup = tw.div`
  grid grid-cols-1 md:grid-cols-3 gap-4
`;

export const Label = tw.label`
  text-sm text-black font-medium
`;

export const Input = tw.input`
  border border-gray-300 rounded-md p-2 w-full text-black placeholder-gray-500
  focus:outline-none focus:ring-2 focus:ring-teal-400
`;

export const ButtonRow = tw.div`
  flex
  justify-center       
  items-center         
  gap-8            
  mt-12                
  mb-10               
  flex-wrap            
`;

export const BigButton = tw.button`
  bg-teal-500
  text-white
  font-semibold
  px-12
  py-4
  rounded-lg
  shadow-md
  hover:bg-teal-600
  transition-all
  duration-200
  w-60                  
  text-center
`;

export const Divider = tw.hr`
  my-8 border-t border-gray-[#3AAFA9]
`;
