"use client";

import Image from "next/image";
import { useLogin } from "../API/Login/useLogin";
import {
  Container,
  Content,
  LogoContainer,
  Form,
  InputGroup,
  Label,
  Input,
  Button,
  ErrorText,
} from "./styles";

export default function LoginPage() {
  const { form, loading, error, handleChange, handleSubmit } = useLogin();

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

        <Form onSubmit={handleSubmit}>
          <InputGroup>
            <Label>Email</Label>
            <Input
              type="email"
              name="email"
              placeholder="Digite seu email de acesso..."
              value={form.email}
              onChange={handleChange}
            />
          </InputGroup>

          <InputGroup>
            <Label>Senha</Label>
            <Input
              type="password"
              name="senha"
              placeholder="Digite sua senha..."
              value={form.senha}
              onChange={handleChange}
            />
          </InputGroup>

          <Button type="submit" disabled={loading}>
            {loading ? "Enviando..." : "Entrar"}
          </Button>
        </Form>

        {error && <ErrorText>{error}</ErrorText>}
      </Content>
    </Container>
  );
}
