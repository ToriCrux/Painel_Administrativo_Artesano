------------ Estoque ------------
CREATE TABLE IF NOT EXISTS tb_estoque (
    id            BIGSERIAL PRIMARY KEY,
    produto_id    BIGINT       NOT NULL,
    saldo         BIGINT       NOT NULL DEFAULT 0,
    versao        BIGINT       NOT NULL DEFAULT 0,
    criado_em     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_estoque_produto UNIQUE (produto_id),
    CONSTRAINT ck_estoque_saldo_nao_negativo CHECK (saldo >= 0)
);

CREATE INDEX IF NOT EXISTS ix_estoque_produto ON tb_estoque (produto_id);

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