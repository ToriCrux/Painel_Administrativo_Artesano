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

CREATE INDEX IF NOT EXISTS idx_cliente_nome ON tb_cliente(nome);
CREATE INDEX IF NOT EXISTS idx_cliente_cidade ON tb_cliente(cidade);