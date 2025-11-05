-- Cria tabela de imagens de produto (armazenamento BYTEA)
CREATE TABLE IF NOT EXISTS tb_produto_imagem (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL REFERENCES tb_produto(id) ON DELETE CASCADE,
    data BYTEA NOT NULL,
    content_type VARCHAR(100),
    nome_arquivo VARCHAR(255),
    tamanho_bytes BIGINT,
    principal BOOLEAN,
    ordem INTEGER,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_tb_produto_imagem_produto ON tb_produto_imagem (produto_id);
CREATE INDEX IF NOT EXISTS idx_tb_produto_imagem_principal ON tb_produto_imagem (produto_id, principal);