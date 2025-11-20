CREATE TABLE IF NOT EXISTS tb_movimentacao_estoque (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL REFERENCES tb_produto(id) ON DELETE CASCADE,
    tipo VARCHAR(20) NOT NULL, -- ENTRADA, SAIDA, AJUSTE
    quantidade BIGINT NOT NULL,
    saldo_anterior BIGINT NOT NULL,
    saldo_final BIGINT NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_movimentacao_produto ON tb_movimentacao_estoque(produto_id);
