------------ Estoque ------------

CREATE TABLE IF NOT EXISTS tb_estoque (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL UNIQUE REFERENCES tb_produto(id) ON DELETE CASCADE,
    quantidade_atual INT NOT NULL DEFAULT 0 CHECK (quantidade_atual >= 0),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Índice para facilitar buscas por produto
CREATE INDEX IF NOT EXISTS idx_estoque_produto ON tb_estoque (produto_id);
