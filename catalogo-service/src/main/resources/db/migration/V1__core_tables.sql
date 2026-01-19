-- =====================================================================
-- SCRIPT COMPLETO (DO ZERO) - CATEGORIAS / CORES / PRODUTOS
-- + CAMPOS DE EXIBICAO (PADRAO + OVERRIDES)
-- + EXCLUSOES DE EXIBICAO POR PRODUTO (NOVO)
-- =====================================================================

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
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_cor_grupo_id ON tb_cor(grupo_id);
CREATE INDEX IF NOT EXISTS idx_cor_nome_lower ON tb_cor(lower(nome));

-- raiz: nome unico (case-insensitive) quando grupo_id IS NULL
CREATE UNIQUE INDEX IF NOT EXISTS ux_cor_root_nome
ON tb_cor (lower(nome))
WHERE grupo_id IS NULL;

-- subcores: nome unico por grupo (case-insensitive) quando grupo_id IS NOT NULL
CREATE UNIQUE INDEX IF NOT EXISTS ux_cor_sub_nome_por_grupo
ON tb_cor (grupo_id, lower(nome))
WHERE grupo_id IS NOT NULL;

-- ======================================
-- TABELA: PRODUTO (COM DETALHES TECNICOS FLEXIVEIS)
-- ======================================
CREATE TABLE IF NOT EXISTS tb_produto (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(60) NOT NULL,
    nome VARCHAR(120) NOT NULL,

    detalhes_tecnicos JSONB NOT NULL DEFAULT '{}'::jsonb,

    preco_unitario NUMERIC(19,2) NOT NULL CHECK (preco_unitario >= 0),
    descricao TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_produto_codigo_lower ON tb_produto (LOWER(codigo));
CREATE INDEX IF NOT EXISTS idx_produto_nome ON tb_produto (nome);

-- (Opcional) GIN pro JSON
-- CREATE INDEX IF NOT EXISTS idx_produto_detalhes_tecnicos_gin
-- ON tb_produto USING GIN (detalhes_tecnicos);

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
-- TABELAS DE USUARIO E PERMISSOES
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
CREATE INDEX IF NOT EXISTS idx_usuario_email ON tb_usuario(email);

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
-- CAMPOS DE EXIBICAO PADRAO
-- (DEFAULT = tudo visivel. Voce pode ir adicionando mais campos aqui)
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
    ('ativo', true),
    ('detalhesTecnicos', true),
    ('categorias', true),
    ('cores', true)
ON CONFLICT DO NOTHING;

-- ======================================
-- PRODUTO x CAMPOS DE EXIBICAO PERSONALIZADOS (OVERRIDES)
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

-- ======================================
-- ✅ NOVO: EXCLUSOES DE EXIBICAO POR PRODUTO
-- (A: default exibe tudo, e voce salva so o que quer esconder)
-- tipo:
--   CATEGORIA, SUBCATEGORIA, ITEM, COR_GRUPO, SUBCOR, DETALHE_CHAVE
-- Para DETALHE_CHAVE usa "chave"
-- Para os demais usa "ref_id"
-- ======================================
CREATE TABLE IF NOT EXISTS tb_produto_exibicao_exclusao (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL REFERENCES tb_produto(id) ON DELETE CASCADE,
    tipo VARCHAR(30) NOT NULL,
    ref_id BIGINT NULL,
    chave VARCHAR(120) NULL,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Unicidade por produto + tipo + referencia/chave
CREATE UNIQUE INDEX IF NOT EXISTS uq_produto_exibicao_exclusao
ON tb_produto_exibicao_exclusao (produto_id, tipo, ref_id, chave);

CREATE INDEX IF NOT EXISTS idx_produto_exibicao_exclusao_produto
ON tb_produto_exibicao_exclusao (produto_id);

-- ======================================
-- SEED MINIMO
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

-- PRODUTOS (detalhes_tecnicos)
INSERT INTO tb_produto (codigo, nome, detalhes_tecnicos, preco_unitario, descricao, ativo)
VALUES
 ('TL-001', 'Ladrilho Coliseu', '{"medidas":"20x20","acabamento":"fosco"}'::jsonb, 49.90, 'Ladrilho hidráulico estilo clássico com acabamento fosco.', TRUE),
 ('TL-002', 'Ladrilho Viena',   '{"medidas":"20x20","espessura":"20mm"}'::jsonb, 54.90, 'Ladrilho com padrão geométrico sofisticado.', TRUE),
 ('TL-003', 'Ladrilho Siena',   '{"medidas":"20x20"}'::jsonb, 59.90, 'Ladrilho floral com tons suaves e acabamento artesanal.', TRUE)
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

-- SUBCORES do "Chocolate Branco"
INSERT INTO tb_cor (nome, hex, ativo, grupo_id)
VALUES
 ('Chocolate Amargo', '#3E2723', TRUE,
  (SELECT id FROM tb_cor WHERE lower(nome) = lower('Chocolate Branco') AND grupo_id IS NULL)),
 ('Chocolate Meio Amargo', '#5D4037', TRUE,
  (SELECT id FROM tb_cor WHERE lower(nome) = lower('Chocolate Branco') AND grupo_id IS NULL))
ON CONFLICT DO NOTHING;

-- PRODUTO x COR (exemplo)
INSERT INTO tb_produto_cor (produto_id, cor_id)
SELECT p.id, c.id
FROM tb_produto p
JOIN tb_cor c ON (
    (p.codigo = 'TL-001' AND lower(c.nome) = lower('Branco') AND c.grupo_id IS NULL) OR
    (p.codigo = 'TL-002' AND lower(c.nome) = lower('Chocolate Branco') AND c.grupo_id IS NULL) OR
    (p.codigo = 'TL-002' AND lower(c.nome) = lower('Chocolate Amargo') AND c.grupo_id = (SELECT id FROM tb_cor WHERE lower(nome)=lower('Chocolate Branco') AND grupo_id IS NULL))
)
ON CONFLICT DO NOTHING;

-- ======================================
-- EXEMPLOS DE EXCLUSAO (opcional)
-- Ex: TL-002 nao quer exibir a chave "espessura" e uma subcor específica
-- ======================================
INSERT INTO tb_produto_exibicao_exclusao (produto_id, tipo, chave)
SELECT p.id, 'DETALHE_CHAVE', 'espessura'
FROM tb_produto p
WHERE p.codigo = 'TL-002'
ON CONFLICT DO NOTHING;

INSERT INTO tb_produto_exibicao_exclusao (produto_id, tipo, ref_id)
SELECT p.id, 'SUBCOR', sc.id
FROM tb_produto p
JOIN tb_cor grp ON lower(grp.nome)=lower('Chocolate Branco') AND grp.grupo_id IS NULL
JOIN tb_cor sc ON sc.grupo_id = grp.id AND lower(sc.nome)=lower('Chocolate Meio Amargo')
WHERE p.codigo = 'TL-002'
ON CONFLICT DO NOTHING;
