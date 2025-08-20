-- =====================================
-- V2 - Seed mínimo para desenvolvimento
-- =====================================

-- Roles
INSERT INTO tb_role (nome) VALUES ('ADMIN'), ('OPERADOR');

-- Usuário admin (hash placeholder; você pode trocar depois)
INSERT INTO tb_usuario (nome, email, password_hash, ativo)
VALUES ('Admin', 'admin@ladrilhos.com', 'admin', TRUE);

-- Vincular admin -> ADMIN
INSERT INTO tb_usuario_role (usuario_id, role_id)
SELECT u.id, r.id
FROM tb_usuario u, tb_role r
WHERE u.email = 'admin@ladrilhos.com'
  AND r.nome = 'ADMIN';

-- Produtos de exemplo
INSERT INTO tb_produto (sku, nome, categoria, medidas, cores, preco_unitario, ativo)
VALUES
 ('TL-001', 'Ladrilho Coliseu', 'Classico',  '20x20', 'preto/branco', 49.90, TRUE),
 ('TL-002', 'Ladrilho Viena',   'Geometrico','20x20', 'cinza',        54.90, TRUE),
 ('TL-003', 'Ladrilho Siena',   'Florais',   '20x20', 'azul/branco',  59.90, TRUE);

-- Estoque inicial
INSERT INTO tb_estoque (produto_id, quantidade_atual, minimo)
SELECT id, 100, 10 FROM tb_produto WHERE sku IN ('TL-001','TL-002','TL-003');

-- Movimentações iniciais (ENTRADA) para documentar o saldo
INSERT INTO tb_movimentacao_estoque (produto_id, tipo, origem, ref_tipo, ref_id, quantidade, saldo_anterior, saldo_posterior)
SELECT p.id, 'ENTRADA', 'AJUSTE', 'AJUSTE', NULL,
       100, 0, 100
FROM tb_produto p WHERE p.sku IN ('TL-001','TL-002','TL-003');
