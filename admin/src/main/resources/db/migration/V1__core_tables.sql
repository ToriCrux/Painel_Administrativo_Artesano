------------ Categorias ------------
CREATE TABLE IF NOT EXISTS tb_categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NULL
);

------------ Cores ------------
CREATE TABLE IF NOT EXISTS tb_cor (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    hex VARCHAR(7),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NULL
);

------------ Produtos ------------
CREATE TABLE IF NOT EXISTS tb_produto (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(60) NOT NULL,
    nome VARCHAR(120) NOT NULL,
    categoria_id BIGINT NOT NULL REFERENCES tb_categoria(id) ON DELETE RESTRICT,
    medidas VARCHAR(120),
    preco_unitario NUMERIC(19,2) NOT NULL CHECK (preco_unitario >= 0),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NULL
);

CREATE UNIQUE INDEX uq_produto_codigo_lower ON tb_produto (LOWER(codigo));
CREATE INDEX idx_produto_nome ON tb_produto (nome);
CREATE INDEX idx_produto_categoria ON tb_produto (categoria_id);

------------ Relação Produto x Cor ------------
CREATE TABLE IF NOT EXISTS tb_produto_cor (
    produto_id BIGINT NOT NULL REFERENCES tb_produto(id) ON DELETE CASCADE,
    cor_id BIGINT NOT NULL REFERENCES tb_cor(id) ON DELETE CASCADE,
    PRIMARY KEY (produto_id, cor_id)
);

CREATE INDEX idx_produto_cor_produto ON tb_produto_cor (produto_id);
CREATE INDEX idx_produto_cor_cor ON tb_produto_cor (cor_id);

------------ Usuário e Role ------------
CREATE TABLE IF NOT EXISTS tb_usuario (
  id               BIGSERIAL PRIMARY KEY,
  nome             VARCHAR(120) NOT NULL,
  email            VARCHAR(160) NOT NULL,
  password_hash    VARCHAR(255) NOT NULL,
  ativo            BOOLEAN NOT NULL DEFAULT TRUE,
  criado_em        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em    TIMESTAMPTZ NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_usuario_email_lower
  ON tb_usuario (LOWER(email));

CREATE TABLE IF NOT EXISTS tb_role (
  id   BIGSERIAL PRIMARY KEY,
  nome VARCHAR(40) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_role_nome_lower
  ON tb_role (LOWER(nome));

CREATE TABLE IF NOT EXISTS tb_usuario_role (
  usuario_id BIGINT NOT NULL REFERENCES tb_usuario(id) ON DELETE CASCADE,
  role_id    BIGINT NOT NULL REFERENCES tb_role(id) ON DELETE CASCADE,
  PRIMARY KEY (usuario_id, role_id)
);

------------ Cliente ------------
CREATE TABLE IF NOT EXISTS tb_cliente (
  id            BIGSERIAL PRIMARY KEY,
  nome          VARCHAR(120) NOT NULL,
  cpf_cnpj      VARCHAR(20)  NOT NULL UNIQUE,
  telefone      VARCHAR(15),
  email         VARCHAR(120) NOT NULL,
  cep           VARCHAR(10),
  endereco      VARCHAR(150),
  numero        VARCHAR(20),
  complemento   VARCHAR(100),
  bairro        VARCHAR(80),
  cidade        VARCHAR(80),
  uf            VARCHAR(2),
  referencia    VARCHAR(150),
  criado_em     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMPTZ NULL
);

CREATE INDEX IF NOT EXISTS ix_cliente_nome ON tb_cliente (nome);
CREATE INDEX IF NOT EXISTS ix_cliente_cpf_cnpj ON tb_cliente (cpf_cnpj);

------------ Proposta ------------
CREATE TABLE IF NOT EXISTS tb_proposta (
  id             BIGSERIAL PRIMARY KEY,
  cliente_id     BIGINT NOT NULL REFERENCES tb_cliente(id) ON DELETE CASCADE,
  codigo         VARCHAR(50) NOT NULL UNIQUE,
  nome_vendedor  VARCHAR(120) NOT NULL,
  data_proposta  DATE NOT NULL,
  data_validade  DATE NOT NULL,
  total          NUMERIC(15,2) NOT NULL DEFAULT 0,
  criado_em      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em  TIMESTAMPTZ NULL
);

CREATE INDEX IF NOT EXISTS ix_proposta_codigo ON tb_proposta (codigo);

------------ Produto da Proposta ------------
CREATE TABLE IF NOT EXISTS tb_produto_proposta (
  id             BIGSERIAL PRIMARY KEY,
  proposta_id    BIGINT NOT NULL REFERENCES tb_proposta(id) ON DELETE CASCADE,
  codigo_produto VARCHAR(50) NOT NULL,
  nome_produto   VARCHAR(150) NOT NULL,
  quantidade     INT NOT NULL CHECK (quantidade > 0),
  preco_unitario NUMERIC(15,2) NOT NULL CHECK (preco_unitario >= 0),
  subtotal       NUMERIC(15,2) NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS ix_produto_proposta_codigo ON tb_produto_proposta (codigo_produto);
CREATE INDEX IF NOT EXISTS ix_produto_proposta_proposta ON tb_produto_proposta (proposta_id);

------------ Índices úteis ------------
CREATE INDEX IF NOT EXISTS idx_usuario_email ON tb_usuario(email);
CREATE INDEX IF NOT EXISTS idx_cliente_nome ON tb_cliente(nome);
CREATE INDEX IF NOT EXISTS idx_cliente_cidade ON tb_cliente(cidade);
