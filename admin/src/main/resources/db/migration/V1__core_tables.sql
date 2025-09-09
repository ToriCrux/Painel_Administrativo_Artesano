------------ Categorias ------------
CREATE TABLE IF NOT EXISTS tb_categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()
);

------------ Cores ------------
CREATE TABLE IF NOT EXISTS tb_cor (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    hex VARCHAR(7),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()
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
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
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
  atualizado_em    TIMESTAMPTZ NOT NULL DEFAULT NOW()
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

------------ Cliente / Proposta / Pedido ------------
CREATE TABLE IF NOT EXISTS tb_cliente (
  id            BIGSERIAL PRIMARY KEY,
  nome          VARCHAR(160) NOT NULL,
  cpf_cnpj      VARCHAR(20)  NOT NULL UNIQUE,
  email         VARCHAR(160) NOT NULL UNIQUE,
  telefone      VARCHAR(30),
  cep           VARCHAR(15),
  endereco      VARCHAR(160),
  numero        VARCHAR(20),
  complemento   VARCHAR(80),
  bairro        VARCHAR(80),
  cidade        VARCHAR(80),
  uf            VARCHAR(2),
  referencia    VARCHAR(160),
  criado_em     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tb_proposta (
  id             BIGSERIAL PRIMARY KEY,
  id_cliente     BIGINT REFERENCES tb_cliente(id),
  id_usuario     BIGINT,
  status         VARCHAR(12) NOT NULL,
  total          NUMERIC(12,2) NOT NULL DEFAULT 0,
  data_proposta  DATE NOT NULL DEFAULT CURRENT_DATE,
  data_validade  DATE,
  criado_em      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em  TIMESTAMPTZ,
  version        INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tb_pedido (
  id                    BIGSERIAL PRIMARY KEY,
  id_proposta           BIGINT REFERENCES tb_proposta(id),
  id_cliente            BIGINT NOT NULL REFERENCES tb_cliente(id),
  id_usuario            BIGINT,
  status                VARCHAR(12) NOT NULL,
  total                 NUMERIC(12,2) NOT NULL DEFAULT 0,
  data_pedido           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  data_entrega_prevista DATE,
  data_entrega          DATE,
  criado_em             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em         TIMESTAMPTZ,
  version               INT NOT NULL DEFAULT 0
);

------------ Índices úteis ------------
CREATE INDEX IF NOT EXISTS idx_usuario_email        ON tb_usuario(email);
CREATE INDEX IF NOT EXISTS idx_cliente_nome         ON tb_cliente(nome);
CREATE INDEX IF NOT EXISTS idx_cliente_cidade       ON tb_cliente(cidade);
CREATE INDEX IF NOT EXISTS idx_proposta_status_data ON tb_proposta(status, data_proposta);
CREATE INDEX IF NOT EXISTS idx_pedido_status_data   ON tb_pedido(status, data_pedido);
