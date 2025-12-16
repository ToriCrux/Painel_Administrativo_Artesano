CREATE TABLE IF NOT EXISTS tb_pedido (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    produto_id BIGINT NOT NULL,
    nome_cliente VARCHAR(120) NOT NULL,
    produto VARCHAR(150) NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario NUMERIC(15,2) NOT NULL,
    total NUMERIC(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT uq_pedido_codigo_produto UNIQUE (codigo, produto_id)
);
