import tw from "tailwind-styled-components";

// Cores convertidas de Tailwind → HEX para compatibilidade total com html2canvas
// teal-600 → #0d9488
// teal-500 → #14b8a6
// teal-400 → #2dd4bf
// gray-700 → #374151
// gray-300 → #d1d5db
// gray-50  → #f9fafb
// placeholder-gray-400 → #9ca3af

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
  font-semibold
  tracking-wide
  text-[#0d9488]   /* teal-600 */
`;

export const Section = tw.div`
  mb-8
`;

export const Label = tw.label`
  block
  font-medium
  mb-2
  text-[#374151]   /* gray-700 */
`;

export const Input = tw.input`
  w-[50%]
  p-2
  border
  rounded-md
  text-[#000000]
  border-[#d1d5db]
  focus:ring-2
  focus:ring-[#2dd4bf]
  focus:outline-none
  placeholder-[#9ca3af]
`;

export const Table = tw.table`
  w-full
  border-collapse
  text-left
  mt-4
`;

export const TableHeader = tw.th`
  p-3
  font-semibold
  border
  bg-[#14b8a6]
  text-[#ffffff]
`;

export const TableRow = tw.tr`
  border-b
  hover:bg-[#f9fafb]
`;

export const TableCell = tw.td`
  p-3
  border
  text-[#374151]
`;

export const LinkButton = tw.button`
  px-4
  py-1
  font-semibold
  rounded-md
  transition
  text-[#ffffff]
  bg-[#14b8a6]
  hover:opacity-90
`;
