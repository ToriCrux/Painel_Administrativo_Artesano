------------ Usuário e Role (PostgreSQL) ------------

CREATE TABLE IF NOT EXISTS tb_usuario (
  id               BIGSERIAL PRIMARY KEY,
  nome             VARCHAR(120) NOT NULL,
  email            VARCHAR(160) NOT NULL,
  password_hash    VARCHAR(255) NOT NULL,
  ativo            BOOLEAN NOT NULL DEFAULT TRUE,
  criado_em        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Unicidade case-insensitive de email
CREATE UNIQUE INDEX IF NOT EXISTS uq_usuario_email_lower
  ON tb_usuario (LOWER(email));

CREATE TABLE IF NOT EXISTS tb_role (
  id   BIGSERIAL PRIMARY KEY,
  nome VARCHAR(40) NOT NULL
);

-- Unicidade case-insensitive de role
CREATE UNIQUE INDEX IF NOT EXISTS uq_role_nome_lower
  ON tb_role (LOWER(nome));

CREATE TABLE IF NOT EXISTS tb_usuario_role (
  usuario_id BIGINT NOT NULL REFERENCES tb_usuario(id) ON DELETE CASCADE,
  role_id    BIGINT NOT NULL REFERENCES tb_role(id) ON DELETE CASCADE,
  PRIMARY KEY (usuario_id, role_id)
);

------------ Catálogo / Estoque / Vendas ------------

CREATE TABLE IF NOT EXISTS tb_produto (
  id               BIGSERIAL PRIMARY KEY,
  sku              VARCHAR(50)  NOT NULL UNIQUE,
  nome             VARCHAR(120) NOT NULL,
  categoria        VARCHAR(80),
  medidas          VARCHAR(80),
  cores            VARCHAR(120),
  preco_unitario   NUMERIC(12,2) NOT NULL,
  ativo            BOOLEAN NOT NULL DEFAULT TRUE,
  criado_em        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em    TIMESTAMPTZ,
  version          INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tb_estoque (
  produto_id        BIGINT PRIMARY KEY REFERENCES tb_produto(id) ON DELETE CASCADE,
  quantidade_atual  INT NOT NULL DEFAULT 0,
  minimo            INT,
  atualizado_em     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  version           INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tb_movimentacao_estoque (
  id               BIGSERIAL PRIMARY KEY,
  produto_id       BIGINT NOT NULL REFERENCES tb_produto(id),
  tipo             VARCHAR(10)  NOT NULL,   -- 'ENTRADA' | 'SAIDA'
  origem           VARCHAR(15)  NOT NULL,   -- 'PRODUCAO' | 'PEDIDO'
  quantidade       INT NOT NULL CHECK (quantidade > 0),
  saldo_anterior   INT,
  saldo_posterior  INT,
  id_usuario       BIGINT,
  ocorrido_em      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

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
  status         VARCHAR(12) NOT NULL,  -- 'APROVADA'|'NEGADA'
  total          NUMERIC(12,2) NOT NULL DEFAULT 0,
  data_proposta  DATE NOT NULL DEFAULT CURRENT_DATE,
  data_validade  DATE,
  criado_em      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em  TIMESTAMPTZ,
  version        INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tb_proposta_item (
  id              BIGSERIAL PRIMARY KEY,
  id_proposta     BIGINT NOT NULL REFERENCES tb_proposta(id) ON DELETE CASCADE,
  id_produto      BIGINT NOT NULL REFERENCES tb_produto(id),
  quantidade      INT NOT NULL CHECK (quantidade > 0),
  preco_unitario  NUMERIC(12,2) NOT NULL,
  subtotal        NUMERIC(12,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_pedido (
  id                    BIGSERIAL PRIMARY KEY,
  id_proposta           BIGINT REFERENCES tb_proposta(id),
  id_cliente            BIGINT NOT NULL REFERENCES tb_cliente(id),
  id_usuario            BIGINT,
  status                VARCHAR(12) NOT NULL,  -- 'INICIADO'|'EM ENTREGA'|'CONCLUIDO'
  total                 NUMERIC(12,2) NOT NULL DEFAULT 0,
  data_pedido           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  data_entrega_prevista DATE,
  data_entrega          DATE,
  criado_em             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em         TIMESTAMPTZ,
  version               INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tb_pedido_item (
  id              BIGSERIAL PRIMARY KEY,
  id_pedido       BIGINT NOT NULL REFERENCES tb_pedido(id) ON DELETE CASCADE,
  id_produto      BIGINT NOT NULL REFERENCES tb_produto(id),
  quantidade      INT NOT NULL CHECK (quantidade > 0),
  preco_unitario  NUMERIC(12,2) NOT NULL,
  subtotal        NUMERIC(12,2) NOT NULL
);

------------ Índices úteis ------------

CREATE INDEX IF NOT EXISTS idx_usuario_email           ON tb_usuario(email);
CREATE INDEX IF NOT EXISTS idx_produto_sku             ON tb_produto(sku);
CREATE INDEX IF NOT EXISTS idx_movestoque_prod_data    ON tb_movimentacao_estoque(produto_id, ocorrido_em);
CREATE INDEX IF NOT EXISTS idx_cliente_nome            ON tb_cliente(nome);
CREATE INDEX IF NOT EXISTS idx_cliente_cidade          ON tb_cliente(cidade);
CREATE INDEX IF NOT EXISTS idx_proposta_status_data    ON tb_proposta(status, data_proposta);
CREATE INDEX IF NOT EXISTS idx_prop_item_proposta      ON tb_proposta_item(id_proposta);
CREATE INDEX IF NOT EXISTS idx_prop_item_produto       ON tb_proposta_item(id_produto);
CREATE INDEX IF NOT EXISTS idx_pedido_status_data      ON tb_pedido(status, data_pedido);
CREATE INDEX IF NOT EXISTS idx_ped_item_pedido         ON tb_pedido_item(id_pedido);
CREATE INDEX IF NOT EXISTS idx_ped_item_produto        ON tb_pedido_item(id_produto);
