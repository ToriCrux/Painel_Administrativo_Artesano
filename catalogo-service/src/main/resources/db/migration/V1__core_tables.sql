-- ======================================
-- TABELA: CATEGORIA
-- ======================================
CREATE TABLE IF NOT EXISTS tb_categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NULL
);

CREATE INDEX IF NOT EXISTS idx_categoria_nome ON tb_categoria (LOWER(nome));

-- ======================================
-- TABELA: SUBCATEGORIA
-- ======================================
CREATE TABLE IF NOT EXISTS tb_subcategoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    categoria_id BIGINT NOT NULL REFERENCES tb_categoria(id) ON DELETE CASCADE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NULL
);

CREATE UNIQUE INDEX uq_subcategoria_nome_categoria ON tb_subcategoria (LOWER(nome), categoria_id);
CREATE INDEX IF NOT EXISTS idx_subcategoria_categoria_id ON tb_subcategoria (categoria_id);

-- ======================================
-- TABELA: ITEM DE CATEGORIA
-- ======================================
CREATE TABLE IF NOT EXISTS tb_item_categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    subcategoria_id BIGINT NOT NULL REFERENCES tb_subcategoria(id) ON DELETE CASCADE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NULL
);

CREATE UNIQUE INDEX uq_item_nome_subcategoria ON tb_item_categoria (LOWER(nome), subcategoria_id);
CREATE INDEX IF NOT EXISTS idx_item_subcategoria_id ON tb_item_categoria (subcategoria_id);

-- ======================================
-- TABELA: COR
-- ======================================
CREATE TABLE IF NOT EXISTS tb_cor (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    hex VARCHAR(7),
    grupo_id BIGINT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NULL,
    CONSTRAINT fk_cor_grupo FOREIGN KEY (grupo_id)
        REFERENCES tb_cor(id)
        ON DELETE SET NULL,
    CONSTRAINT uq_cor_nome_grupo UNIQUE (nome, grupo_id)
);

CREATE INDEX IF NOT EXISTS idx_cor_grupo_id ON tb_cor(grupo_id);

-- ======================================
-- TABELA: PRODUTO
-- (❌ sem coluna item_categoria_id — relacionamento via join table)
-- ======================================
CREATE TABLE IF NOT EXISTS tb_produto (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(60) NOT NULL,
    nome VARCHAR(120) NOT NULL,
    medidas VARCHAR(120),
    preco_unitario NUMERIC(19,2) NOT NULL CHECK (preco_unitario >= 0),
    descricao TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NULL
);

CREATE UNIQUE INDEX uq_produto_codigo_lower ON tb_produto (LOWER(codigo));
CREATE INDEX idx_produto_nome ON tb_produto (nome);

-- ======================================
-- NOVA TABELA: PRODUTO x ITEM_CATEGORIA (ManyToMany)
-- ======================================
CREATE TABLE IF NOT EXISTS tb_produto_item_categoria (
    produto_id BIGINT NOT NULL REFERENCES tb_produto(id) ON DELETE CASCADE,
    item_categoria_id BIGINT NOT NULL REFERENCES tb_item_categoria(id) ON DELETE CASCADE,
    PRIMARY KEY (produto_id, item_categoria_id)
);

CREATE INDEX idx_produto_item_categoria_produto ON tb_produto_item_categoria (produto_id);
CREATE INDEX idx_produto_item_categoria_item ON tb_produto_item_categoria (item_categoria_id);

-- ======================================
-- TABELA: PRODUTO x COR
-- ======================================
CREATE TABLE IF NOT EXISTS tb_produto_cor (
    produto_id BIGINT NOT NULL REFERENCES tb_produto(id) ON DELETE CASCADE,
    cor_id BIGINT NOT NULL REFERENCES tb_cor(id) ON DELETE CASCADE,
    PRIMARY KEY (produto_id, cor_id)
);

CREATE INDEX idx_produto_cor_produto ON tb_produto_cor (produto_id);
CREATE INDEX idx_produto_cor_cor ON tb_produto_cor (cor_id);

-- ======================================
-- TABELAS DE USUÁRIO E PERMISSÕES
-- ======================================
CREATE TABLE IF NOT EXISTS tb_usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_usuario_email_lower ON tb_usuario (LOWER(email));

CREATE TABLE IF NOT EXISTS tb_role (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(40) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_role_nome_lower ON tb_role (LOWER(nome));

CREATE TABLE IF NOT EXISTS tb_usuario_role (
    usuario_id BIGINT NOT NULL REFERENCES tb_usuario(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES tb_role(id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, role_id)
);

-- ======================================
-- TABELA: CAMPOS DE EXIBIÇÃO PADRÃO
-- ======================================
CREATE TABLE IF NOT EXISTS tb_campo_exibicao_padrao (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    visivel_padrao BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO tb_campo_exibicao_padrao (nome, visivel_padrao)
VALUES
    ('codigo', true),
    ('nome', true),
    ('descricao', true),
    ('precoUnitario', true),
    ('ativo', true)
ON CONFLICT DO NOTHING;

-- ======================================
-- TABELA: PRODUTO x CAMPOS DE EXIBIÇÃO PERSONALIZADOS
-- ======================================
CREATE TABLE IF NOT EXISTS tb_produto_campo_exibicao (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL REFERENCES tb_produto(id) ON DELETE CASCADE,
    campo VARCHAR(50) NOT NULL,
    visivel BOOLEAN NOT NULL DEFAULT TRUE,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (produto_id, campo)
);

CREATE INDEX IF NOT EXISTS idx_produto_campo_exibicao_produto ON tb_produto_campo_exibicao (produto_id);

-- ======================================
-- INDEX EXTRA
-- ======================================
CREATE INDEX IF NOT EXISTS idx_usuario_email ON tb_usuario(email);
