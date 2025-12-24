CREATE TABLE IF NOT EXISTS tb_estoque (
    id              BIGSERIAL PRIMARY KEY,
    produto_id      BIGINT       NOT NULL,
    produto_codigo  VARCHAR(255) NOT NULL DEFAULT '—',
    produto_nome    VARCHAR(255) NOT NULL DEFAULT '(Produto removido)',
    saldo           BIGINT       NOT NULL DEFAULT 0,
    versao          BIGINT       NOT NULL DEFAULT 0,
    criado_em       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    atualizado_em   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    ativo           BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_estoque_produto UNIQUE (produto_id),
    CONSTRAINT ck_estoque_saldo_nao_negativo CHECK (saldo >= 0)
);

-- 🔸 Índice para busca rápida por produto
CREATE INDEX IF NOT EXISTS ix_estoque_produto ON tb_estoque (produto_id);

-- ========================================
-- 🔹 TRIGGER: Atualiza o campo atualizado_em automaticamente
-- ========================================
CREATE OR REPLACE FUNCTION fn_estoque_set_atualizado_em()
RETURNS trigger AS $$
BEGIN
  NEW.atualizado_em := NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS tg_estoque_set_atualizado_em ON tb_estoque;

CREATE TRIGGER tg_estoque_set_atualizado_em
    BEFORE UPDATE ON tb_estoque
    FOR EACH ROW EXECUTE FUNCTION fn_estoque_set_atualizado_em();

-- ========================================
-- 🔹 TABELA: tb_movimentacao_estoque
-- ========================================
CREATE TABLE IF NOT EXISTS tb_movimentacao_estoque (
    id              BIGSERIAL PRIMARY KEY,
    produto_id      BIGINT NOT NULL,
    tipo            VARCHAR(20) NOT NULL, -- ENTRADA, SAIDA, AJUSTE, CLIENTE, CRIACAO
    quantidade      BIGINT NOT NULL,
    saldo_anterior  BIGINT NOT NULL DEFAULT 0,
    saldo_novo      BIGINT NOT NULL DEFAULT 0,
    descricao       VARCHAR(255), -- nova coluna para informações detalhadas
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 🔸 Índice para listagem ordenada e filtragem por produto
CREATE INDEX IF NOT EXISTS idx_movimentacao_produto ON tb_movimentacao_estoque(produto_id);

