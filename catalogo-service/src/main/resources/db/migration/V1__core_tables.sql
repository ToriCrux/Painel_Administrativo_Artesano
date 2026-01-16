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

CREATE UNIQUE INDEX IF NOT EXISTS uq_subcategoria_nome_categoria
ON tb_subcategoria (LOWER(nome), categoria_id);

CREATE INDEX IF NOT EXISTS idx_subcategoria_categoria_id
ON tb_subcategoria (categoria_id);

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

CREATE UNIQUE INDEX IF NOT EXISTS uq_item_nome_subcategoria
ON tb_item_categoria (LOWER(nome), subcategoria_id);

CREATE INDEX IF NOT EXISTS idx_item_subcategoria_id
ON tb_item_categoria (subcategoria_id);

-- ======================================
-- TABELA: COR (✅ CORRIGIDA)
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
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_cor_grupo_id ON tb_cor(grupo_id);
CREATE INDEX IF NOT EXISTS idx_cor_nome_lower ON tb_cor(lower(nome));

-- ✅ raiz: nome único (case-insensitive) quando grupo_id IS NULL
CREATE UNIQUE INDEX IF NOT EXISTS ux_cor_root_nome
ON tb_cor (lower(nome))
WHERE grupo_id IS NULL;

-- ✅ subcores: nome único por grupo (case-insensitive) quando grupo_id IS NOT NULL
CREATE UNIQUE INDEX IF NOT EXISTS ux_cor_sub_nome_por_grupo
ON tb_cor (grupo_id, lower(nome))
WHERE grupo_id IS NOT NULL;

-- ======================================
-- TABELA: PRODUTO
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

CREATE UNIQUE INDEX IF NOT EXISTS uq_produto_codigo_lower ON tb_produto (LOWER(codigo));
CREATE INDEX IF NOT EXISTS idx_produto_nome ON tb_produto (nome);

-- ======================================
-- PRODUTO x ITEM_CATEGORIA (ManyToMany)
-- ======================================
CREATE TABLE IF NOT EXISTS tb_produto_item_categoria (
    produto_id BIGINT NOT NULL REFERENCES tb_produto(id) ON DELETE CASCADE,
    item_categoria_id BIGINT NOT NULL REFERENCES tb_item_categoria(id) ON DELETE CASCADE,
    PRIMARY KEY (produto_id, item_categoria_id)
);

CREATE INDEX IF NOT EXISTS idx_produto_item_categoria_produto ON tb_produto_item_categoria (produto_id);
CREATE INDEX IF NOT EXISTS idx_produto_item_categoria_item ON tb_produto_item_categoria (item_categoria_id);

-- ======================================
-- PRODUTO x COR (ManyToMany)
-- ======================================
CREATE TABLE IF NOT EXISTS tb_produto_cor (
    produto_id BIGINT NOT NULL REFERENCES tb_produto(id) ON DELETE CASCADE,
    cor_id BIGINT NOT NULL REFERENCES tb_cor(id) ON DELETE CASCADE,
    PRIMARY KEY (produto_id, cor_id)
);

CREATE INDEX IF NOT EXISTS idx_produto_cor_produto ON tb_produto_cor (produto_id);
CREATE INDEX IF NOT EXISTS idx_produto_cor_cor ON tb_produto_cor (cor_id);

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
-- CAMPOS DE EXIBIÇÃO PADRÃO
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
-- PRODUTO x CAMPOS DE EXIBIÇÃO PERSONALIZADOS
-- ======================================
CREATE TABLE IF NOT EXISTS tb_produto_campo_exibicao (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL REFERENCES tb_produto(id) ON DELETE CASCADE,
    campo VARCHAR(50) NOT NULL,
    visivel BOOLEAN NOT NULL DEFAULT TRUE,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (produto_id, campo)
);

CREATE INDEX IF NOT EXISTS idx_produto_campo_exibicao_produto
ON tb_produto_campo_exibicao (produto_id);

CREATE INDEX IF NOT EXISTS idx_usuario_email ON tb_usuario(email);

-- ======================================
-- SEED MÍNIMO
-- ======================================

-- CATEGORIAS
INSERT INTO tb_categoria (nome, ativo)
VALUES ('Clássico', TRUE), ('Geométrico', TRUE), ('Florais', TRUE)
ON CONFLICT DO NOTHING;

-- SUBCATEGORIAS
INSERT INTO tb_subcategoria (nome, categoria_id, ativo)
VALUES
 ('Coleção Tradicional', (SELECT id FROM tb_categoria WHERE nome = 'Clássico'), TRUE),
 ('Coleção Moderna', (SELECT id FROM tb_categoria WHERE nome = 'Geométrico'), TRUE),
 ('Coleção Romântica', (SELECT id FROM tb_categoria WHERE nome = 'Florais'), TRUE)
ON CONFLICT DO NOTHING;

-- ITENS
INSERT INTO tb_item_categoria (nome, subcategoria_id, ativo)
VALUES
 ('Ladrilhos Antigos', (SELECT id FROM tb_subcategoria WHERE nome = 'Coleção Tradicional'), TRUE),
 ('Ladrilhos Contemporâneos', (SELECT id FROM tb_subcategoria WHERE nome = 'Coleção Moderna'), TRUE),
 ('Ladrilhos Florais', (SELECT id FROM tb_subcategoria WHERE nome = 'Coleção Romântica'), TRUE)
ON CONFLICT DO NOTHING;

-- PRODUTOS
INSERT INTO tb_produto (codigo, nome, medidas, preco_unitario, descricao, ativo)
VALUES
 ('TL-001', 'Ladrilho Coliseu', '20x20', 49.90, 'Ladrilho hidráulico estilo clássico com acabamento fosco.', TRUE),
 ('TL-002', 'Ladrilho Viena', '20x20', 54.90, 'Ladrilho com padrão geométrico sofisticado.', TRUE),
 ('TL-003', 'Ladrilho Siena', '20x20', 59.90, 'Ladrilho floral com tons suaves e acabamento artesanal.', TRUE)
ON CONFLICT DO NOTHING;

-- PRODUTO x ITEM
INSERT INTO tb_produto_item_categoria (produto_id, item_categoria_id)
SELECT p.id, i.id
FROM tb_produto p
JOIN tb_item_categoria i
  ON ( (p.codigo = 'TL-001' AND i.nome = 'Ladrilhos Antigos')
    OR (p.codigo = 'TL-002' AND i.nome = 'Ladrilhos Contemporâneos')
    OR (p.codigo = 'TL-003' AND i.nome = 'Ladrilhos Florais') )
ON CONFLICT DO NOTHING;

-- CORES (raiz)
INSERT INTO tb_cor (nome, hex, ativo, grupo_id)
VALUES
 ('Branco', '#FFFFFF', TRUE, NULL),
 ('Preto', '#000000', TRUE, NULL),
 ('Chocolate Branco', '#EFEFE9', TRUE, NULL)
ON CONFLICT DO NOTHING;

-- SUBCORES do "Chocolate Branco" (exemplo)
INSERT INTO tb_cor (nome, hex, ativo, grupo_id)
VALUES
 ('Chocolate Amargo', '#3E2723', TRUE, (SELECT id FROM tb_cor WHERE lower(nome) = lower('Chocolate Branco') AND grupo_id IS NULL)),
 ('Chocolate Meio Amargo', '#5D4037', TRUE, (SELECT id FROM tb_cor WHERE lower(nome) = lower('Chocolate Branco') AND grupo_id IS NULL))
ON CONFLICT DO NOTHING;
