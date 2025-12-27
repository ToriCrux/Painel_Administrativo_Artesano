-- ===========================================
-- 🔹 V2 - Nova estrutura para pedidos com múltiplos itens
-- ===========================================

-- 1️⃣ Criar nova tabela de itens
CREATE TABLE IF NOT EXISTS tb_item_pedido (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    nome_produto VARCHAR(150) NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario NUMERIC(15,2) NOT NULL,
    total NUMERIC(15,2) NOT NULL,
    pedido_id BIGINT NOT NULL,
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id)
        REFERENCES tb_pedido (id) ON DELETE CASCADE
);

-- 2️⃣ Migrar os dados antigos da estrutura anterior (tb_pedido -> tb_item_pedido)
INSERT INTO tb_item_pedido (produto_id, nome_produto, quantidade, preco_unitario, total, pedido_id)
SELECT produto_id, produto, quantidade, preco_unitario, total, id
FROM tb_pedido;

-- 3️⃣ Remover colunas antigas do modelo anterior
ALTER TABLE tb_pedido
DROP CONSTRAINT IF EXISTS uq_pedido_codigo_produto;

ALTER TABLE tb_pedido
DROP COLUMN IF EXISTS produto_id,
DROP COLUMN IF EXISTS produto,
DROP COLUMN IF EXISTS quantidade,
DROP COLUMN IF EXISTS preco_unitario;

-- 4️⃣ Garantir que os campos atuais do pedido principal estão corretos
ALTER TABLE tb_pedido
ALTER COLUMN total SET NOT NULL,
ALTER COLUMN status SET NOT NULL;

-- 5️⃣ Criar índices úteis
CREATE INDEX IF NOT EXISTS idx_pedido_codigo ON tb_pedido (codigo);
CREATE INDEX IF NOT EXISTS idx_item_pedido_produto_id ON tb_item_pedido (produto_id);
CREATE INDEX IF NOT EXISTS idx_item_pedido_pedido_id ON tb_item_pedido (pedido_id);

