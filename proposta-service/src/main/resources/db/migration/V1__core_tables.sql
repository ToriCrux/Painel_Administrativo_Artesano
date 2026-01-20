-- =====================================================================
-- V1 - CORE TABLES (NOVO - COM PII CRIPTOGRAFADA)
-- - tb_cliente: colunas *_enc e *_hash
-- - UNIQUE em cpf_cnpj_hash (não no texto puro)
-- - colunas legadas (cpf_cnpj/email/telefone/etc) ficam opcionais
-- =====================================================================

-- -------------------------
-- Cliente
-- -------------------------
CREATE TABLE IF NOT EXISTS tb_cliente (
    id            BIGSERIAL PRIMARY KEY,

    nome          VARCHAR(120) NOT NULL,

    -- LEGADO (opcional) - ideal: gravar NULL e usar *_enc
    cpf_cnpj      VARCHAR(20)  NULL,
    telefone      VARCHAR(15)  NULL,
    email         VARCHAR(120) NULL,

    cep           VARCHAR(10)  NULL,
    endereco      VARCHAR(150) NULL,
    numero        VARCHAR(20)  NULL,
    complemento   VARCHAR(100) NULL,
    bairro        VARCHAR(80)  NULL,
    cidade        VARCHAR(80)  NULL,
    uf            VARCHAR(2)   NULL,
    referencia    VARCHAR(150) NULL,

    -- NOVO (criptografia)
    cpf_cnpj_enc     VARCHAR(512) NULL,
    cpf_cnpj_hash    VARCHAR(64)  NOT NULL,
    telefone_enc     VARCHAR(512) NULL,
    telefone_hash    VARCHAR(64)  NULL,
    email_enc        VARCHAR(512) NULL,
    email_hash       VARCHAR(64)  NULL,
    cep_enc          VARCHAR(512) NULL,
    endereco_enc     VARCHAR(512) NULL,
    numero_enc       VARCHAR(512) NULL,
    complemento_enc  VARCHAR(512) NULL,
    bairro_enc       VARCHAR(512) NULL,
    referencia_enc   VARCHAR(512) NULL,

    criado_em     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NULL
);

-- UNIQUE determinístico (substitui UNIQUE do texto puro)
CREATE UNIQUE INDEX IF NOT EXISTS ux_cliente_cpf_cnpj_hash ON tb_cliente (cpf_cnpj_hash);

-- índices úteis
CREATE INDEX IF NOT EXISTS ix_cliente_nome ON tb_cliente (nome);
CREATE INDEX IF NOT EXISTS ix_cliente_email_hash ON tb_cliente (email_hash);
CREATE INDEX IF NOT EXISTS ix_cliente_cidade ON tb_cliente (cidade);

-- (opcional) índice pra buscas por cpf puro (se você ainda usar, o ideal é NÃO usar)
-- CREATE INDEX IF NOT EXISTS ix_cliente_cpf_cnpj ON tb_cliente (cpf_cnpj);


-- -------------------------
-- Proposta
-- -------------------------
CREATE TABLE IF NOT EXISTS tb_proposta (
    id             BIGSERIAL PRIMARY KEY,
    cliente_id     BIGINT NOT NULL REFERENCES tb_cliente(id) ON DELETE CASCADE,

    codigo         VARCHAR(50)  NOT NULL UNIQUE,
    nome_vendedor  VARCHAR(120) NOT NULL,
    data_proposta  DATE NOT NULL,
    data_validade  DATE NOT NULL,
    total          NUMERIC(15,2) NOT NULL DEFAULT 0,

    data_criacao   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    criado_em      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em  TIMESTAMPTZ NULL
);

CREATE INDEX IF NOT EXISTS ix_proposta_codigo ON tb_proposta (codigo);
CREATE INDEX IF NOT EXISTS ix_proposta_data_criacao ON tb_proposta (data_criacao);


-- -------------------------
-- Produto da Proposta
-- -------------------------
CREATE TABLE IF NOT EXISTS tb_produto_proposta (
    id             BIGSERIAL PRIMARY KEY,
    proposta_id    BIGINT NOT NULL REFERENCES tb_proposta(id) ON DELETE CASCADE,

    codigo_produto VARCHAR(50)  NOT NULL,
    nome_produto   VARCHAR(150) NOT NULL,
    quantidade     INT NOT NULL CHECK (quantidade > 0),
    preco_unitario NUMERIC(15,2) NOT NULL CHECK (preco_unitario >= 0),
    subtotal       NUMERIC(15,2) NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS ix_produto_proposta_codigo ON tb_produto_proposta (codigo_produto);
CREATE INDEX IF NOT EXISTS ix_produto_proposta_proposta ON tb_produto_proposta (proposta_id);
