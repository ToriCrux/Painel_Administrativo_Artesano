import tw from "tailwind-styled-components";

export const NavbarContainer = tw.nav`
  flex items-center justify-between bg-[#E0F4FB] px-8 py-4 shadow-sm
`;

export const LogoSection = tw.div`
  flex items-center
`;

export const NavLinks = tw.div`
  flex gap-6
`;

export const NavItem = tw.span<{ $active?: boolean }>`
  text-teal-700 font-medium cursor-pointer relative
  hover:text-teal-900 transition
  ${({ $active }) => $active && "border-b-2 border-teal-600 pb-1"}
`;
