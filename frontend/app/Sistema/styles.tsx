import tw from "tailwind-styled-components";

export const Container = tw.div`
  flex flex-col min-h-screen bg-[#ffffff]
`;

export const Content = tw.div`
  flex flex-col grow px-6
`;

/* Centraliza apenas a Home */
export const HomeCenter = tw.div`
  flex flex-col items-center justify-center text-center min-h-[60vh]
`;

export const Title = tw.h1`
  text-3xl sm:text-4xl font-semibold text-[#36B0AC]
`;

export const Subtitle = tw.p`
  mt-4 text-gray-600 text-base sm:text-lg max-w-xl
`;



