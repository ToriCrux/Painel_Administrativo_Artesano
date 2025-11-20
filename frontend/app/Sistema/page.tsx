"use client";

import { Container, Content, HomeCenter, Title, Subtitle } from "./styles";

export default function SistemaPage() {
  return (
    <Container>
      <Content>
        <HomeCenter>
          <Title>Bem-vindo ao Sistema Artesano</Title>
          <Subtitle>
            Utilize o menu acima para acessar as funcionalidades do sistema.
          </Subtitle>
        </HomeCenter>
      </Content>
    </Container>
  );
}
