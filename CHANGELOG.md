# Atualização para a equipe – 2025-10-22

**Branch nova**: `dev_refactoring_auth_service`

## Resumo Executivo

- **Extração do módulo de autenticação** do projeto `admin` → virou um microserviço dedicado (**AUTH-SERVICE**, que emite JWT).
- O projeto `admin` (Catálogo) agora atua como **Resource Server**: ele **aceita** e **valida** tokens emitidos pelo AUTH-SERVICE, mas **não emite** tokens.
- Integração com **Service Registry (Eureka)** para descoberta dinâmica de serviços.
- Configuração de **Postgres + Flyway** para `dev`, `server.port=8082`, Swagger em `/swagger-ui.html` no perfil `dev`.

---

## Glossário arquitetural (explicado no contexto do projeto)

### 1) Resource Server (este serviço – Catálogo)

**O que é?**  
É o serviço que **exposta a API de negócio** (produtos, categorias, estoque, etc.) e **protege** esses endpoints com **validação de tokens** (JWT).  
Ele **não autentica usuários** diretamente (não pede senha, OTP, etc.). Ele **confia** em um emissor de tokens (o AUTH-SERVICE) e valida cada requisição consultando o token apresentado.

**Por que existe?**

- Permite **separar autenticação/identidade** da **lógica de negócio**, mantendo o Catálogo focado em catálogo.
- Escalabilidade independente: podemos escalar Catálogo e AUTH-SERVICE de forma **autônoma**.
- Segurança mais clara: o Resource Server só precisa de regras para **validar** (`issuer`, `audience`, **assinatura**).

**Como funciona na prática?**

- O cliente chama o Catálogo com `Authorization: Bearer <jwt>`.
- O Catálogo valida: assinatura do JWT (chave/segredo), `iss` (quem emitiu), `aud` (para quem foi emitido), **expiração**, escopos/roles.
- Se válido, a requisição segue. Se inválido/ausente, **401/403**.

---

### 2) Authorization/Authentication Server (AUTH-SERVICE)

**O que é?**  
É o serviço que **autentica** (confere credenciais) e **emite tokens** (JWT). Pode expor endpoints como `/auth/login`, `/auth/refresh`, `/oauth/token`, etc.

**Por que existe?**

- Centraliza regras de **identidade** (usuários, senhas, refresh tokens, políticas de senha, MFA).
- Facilita adoção futura de **padrões** (OAuth2, OpenID Connect), SSO, provedores externos.
- Desacopla o ciclo de vida de identidade do ciclo do Catálogo.

**Como funciona na prática?**

- O cliente envia credenciais para o AUTH-SERVICE.
- Recebe um **JWT** assinado (e opcionalmente um refresh token).
- Usa esse JWT para chamar o **Resource Server** (Catálogo).

> Dica: garantir que o **mesmo segredo/chave pública** configurado no Catálogo seja compatível com o emissor (claim `iss`) e com a **assinatura** do AUTH-SERVICE.

---

### 3) Service Registry (Eureka)

**O que é?**  
É um **catálogo dinâmico** de serviços e seus endereços (instances/URLs). Os serviços **se registram** e **descobrem** uns aos outros sem precisar de hostnames fixos.

**Por que existe?**

- Em ambientes distribuídos e com containers, instâncias sobem/caem e IPs mudam. O registry evita **config hard-coded**.
- Permite **load balancing** e **failover** (o cliente descobre várias instâncias elegíveis).
- Facilita observabilidade (quem está **UP/DOWN** via health check).

**Como funciona na prática?**

- Cada microserviço inicia e **se registra** no Eureka (ex.: `CATALOGO-SERVICE@host:port`).
- Quando o Catálogo precisa chamar outro serviço (ex.: precificação), ele consulta o **registry** em vez de depender de um hostname fixo.

---

## Fluxo ponta-a-ponta (simplificado)

```
   [Usuário/Cliente]
          │  (1) credenciais
          ▼
   [AUTH-SERVICE]  ⇦ autentica e emite JWT (iss, aud, exp, assinatura)
          │  (2) JWT
          ▼
 [CATALOGO - Resource Server]  ⇦ valida JWT (assinatura/iss/aud/exp/roles)
          │  (3) resposta segura
          ▼
      [Cliente]
```

- **Eureka** fica em paralelo ao fluxo acima, mantendo o **mapa de instâncias** dos serviços para chamadas **service-to-service**.

---

## Benefícios práticos dessa separação

1. **Segurança**: superfície de ataque menor por serviço; rotação de chaves/segredos isolada.
2. **Escalabilidade**: aumentar réplicas do AUTH-SERVICE em picos de login sem tocar no Catálogo.
3. **Evolução**: Catálogo evolui o domínio de produtos/estoque sem herdar complexidade de autenticação.
4. **Observabilidade**: com registry + health checks, sabemos quais instâncias estão **UP/DOWN**.
5. **Testabilidade**: contratos de segurança testados via tokens **mockados**/fakes em testes de integração.

---

## Pontos de atenção e erros comuns

- **Segredo/Chave**: o Catálogo precisa validar com a **chave correta** (ou JWKS público). Mismatches causam 401.
- **Issuer/Audience**: claims `iss`/`aud` devem bater com a **config** do Resource Server.
- **Clock Skew**: diferenças de horário entre serviços podem invalidar `exp/nbf`. Ajustar tolerância.
- **Perfis**: em `dev`, muitas vezes usamos segredo **embutido** (apenas para desenvolvimento). Em `prod`, **NUNCA**.
- **Fallback**: se o Eureka estiver indisponível, clientes que dependem de descoberta podem falhar; considere timeouts e **retries**.

---

## O que mudou **nesta** branch (além das explicações)

- **Auth saiu do `admin`**: pacotes `com.sistema.admin.auth.*` e `com.sistema.admin.config.jwt` foram removidos daqui.
- **Catálogo é Resource Server**: todos os endpoints de negócio exigem `Authorization: Bearer <jwt>`.
- **Infra**: Dockerfile multi-stage (Temurin JDK 21); Flyway ativo em `dev`; Swagger em `/swagger-ui.html`.
- **Eureka**: serviço registrado e consultando o registry para descoberta.
