------------ Seed mínimo para desenvolvimento ------------


-- Roles
INSERT INTO tb_role (nome) VALUES ('ADMIN'), ('OPERADOR');

-- Usuário admin
INSERT INTO tb_usuario (nome, email, password_hash, ativo)
VALUES ('Admin', 'admin@ladrilhos.com', 'admin', TRUE);

-- Vincular admin -> ADMIN
INSERT INTO tb_usuario_role (usuario_id, role_id)
SELECT u.id, r.id
FROM tb_usuario u, tb_role r
WHERE u.email = 'admin@ladrilhos.com'
  AND r.nome = 'ADMIN';

-- Produtos
INSERT INTO tb_produto (sku, nome, categoria, medidas, cores, preco_unitario, ativo)
VALUES
 ('TL-001', 'Ladrilho Coliseu', 'Classico',  '20x20', 'preto/branco', 49.90, TRUE),
 ('TL-002', 'Ladrilho Viena',   'Geometrico','20x20', 'cinza',        54.90, TRUE),
 ('TL-003', 'Ladrilho Siena',   'Florais',   '20x20', 'azul/branco',  59.90, TRUE);

-- Estoque inicial
INSERT INTO tb_estoque (produto_id, quantidade_atual, minimo)
SELECT id, 100, 10 FROM tb_produto WHERE sku IN ('TL-001','TL-002','TL-003');

-- Movimentações iniciais
INSERT INTO tb_movimentacao_estoque (produto_id, tipo, origem, quantidade, saldo_anterior, saldo_posterior)
SELECT p.id, 'ENTRADA', 'PRODUCAO', 100, 0, 100
FROM tb_produto p WHERE p.sku IN ('TL-001','TL-002','TL-003');

-- Cliente
INSERT INTO tb_cliente (nome, cpf_cnpj, email, telefone, cep, cidade, uf)
VALUES ('Cliente Demo', '12345678000199', 'cliente@demo.com', '(11) 99999-0000', '01000-000', 'São Paulo', 'SP');

-- proposta
INSERT INTO tb_proposta (id_cliente, status, total, data_proposta, data_validade)
SELECT c.id, 'APROVADA', 0, CURRENT_DATE, CURRENT_DATE + 15
FROM tb_cliente c
WHERE c.email = 'cliente@demo.com';

-- Adiciona 2 itens à proposta
INSERT INTO tb_proposta_item (id_proposta, id_produto, quantidade, preco_unitario, subtotal)
SELECT p.id, pr.id, 5, pr.preco_unitario, 5 * pr.preco_unitario
FROM tb_proposta p
JOIN tb_produto pr ON pr.sku = 'TL-001'
WHERE p.id = (SELECT MAX(id) FROM tb_proposta);

INSERT INTO tb_proposta_item (id_proposta, id_produto, quantidade, preco_unitario, subtotal)
SELECT p.id, pr.id, 3, pr.preco_unitario, 3 * pr.preco_unitario
FROM tb_proposta p
JOIN tb_produto pr ON pr.sku = 'TL-002'
WHERE p.id = (SELECT MAX(id) FROM tb_proposta);

-- Recalcula total da proposta
UPDATE tb_proposta
SET total = (
  SELECT COALESCE(SUM(subtotal),0) FROM tb_proposta_item WHERE id_proposta = tb_proposta.id
)
WHERE id = (SELECT MAX(id) FROM tb_proposta);

-- Pedido a partir da proposta
INSERT INTO tb_pedido (id_proposta, id_cliente, status, total)
SELECT pr.id, pr.id_cliente, 'INICIADO', pr.total
FROM tb_proposta pr
WHERE pr.id = (SELECT MAX(id) FROM tb_proposta);

INSERT INTO tb_pedido_item (id_pedido, id_produto, quantidade, preco_unitario, subtotal)
SELECT ped.id, pi.id_produto, pi.quantidade, pi.preco_unitario, pi.subtotal
FROM tb_pedido ped
JOIN tb_proposta pr ON pr.id = ped.id_proposta
JOIN tb_proposta_item pi ON pi.id_proposta = pr.id
WHERE ped.id = (SELECT MAX(id) FROM tb_pedido);
