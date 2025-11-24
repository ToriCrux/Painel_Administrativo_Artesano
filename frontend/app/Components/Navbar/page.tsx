"use client";

import Image from "next/image";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { NavbarContainer, LogoSection, NavLinks, NavItem } from "./styles";

export default function Navbar() {
  const pathname = usePathname();

  const links = [
    { name: "HOME", href: "/Sistema" },
    { name: "CATÁLOGO", href: "/Sistema/Catalogo" },
    { name: "PRODUTOS", href: "/Sistema/Produtos" },
    { name: "ESTOQUE", href: "/Sistema/Estoque" },
    { name: "PROPOSTAS ORÇAMENTÁRIAS", href: "/Sistema/Proposta" },
    { name: "PEDIDOS", href: "/Sistema/Pedidos" },
    { name: "FILTROS", href: "/Sistema/Filtros" },
  ];

  return (
    <NavbarContainer>
      <LogoSection>
        <Image src="/logo.png" alt="Logo Artesano Brasil" width={150} height={50} priority />
      </LogoSection>

      <NavLinks>
        {links.map((link) => {
          const isActive = pathname === link.href;
          return (
            <Link key={link.href} href={link.href}>
              <NavItem $active={isActive}>{link.name}</NavItem>
            </Link>
          );
        })}
      </NavLinks>
    </NavbarContainer>
  );
}
