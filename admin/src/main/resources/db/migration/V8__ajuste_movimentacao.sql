-- Remove coluna antiga que não faz mais sentido
ALTER TABLE tb_movimentacao_estoque
DROP COLUMN IF EXISTS saldo_final;

-- Garante que colunas corretas existam
ALTER TABLE tb_movimentacao_estoque
ADD COLUMN IF NOT EXISTS saldo_anterior BIGINT NOT NULL DEFAULT 0;

ALTER TABLE tb_movimentacao_estoque
ADD COLUMN IF NOT EXISTS saldo_novo BIGINT NOT NULL DEFAULT 0;
