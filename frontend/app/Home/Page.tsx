"use client";

import React from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { Container, Content, LogoContainer, ButtonGroup, Button } from "./styles";

export default function HomePage() {
  const router = useRouter();

  return (
    <Container>
      <Content>
        <LogoContainer>
          <Image
            src="/logo.png"
            alt="Logo Artesano Brasil"
            width={280}
            height={100}
            priority
          />
        </LogoContainer>

        <ButtonGroup>
          <Button onClick={() => router.push("/Login")}>Acessar</Button>
          <Button>Cadastrar</Button>
        </ButtonGroup>
      </Content>
    </Container>
  );
}
