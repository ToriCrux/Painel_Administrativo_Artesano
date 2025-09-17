------------ Seed mínimo para desenvolvimento ------------

-- Categorias demo
INSERT INTO tb_categoria (nome, ativo)
VALUES
 ('Classico', TRUE),
 ('Geometrico', TRUE),
 ('Florais', TRUE)
ON CONFLICT DO NOTHING;

-- Produtos demo
INSERT INTO tb_produto (codigo, nome, categoria_id, medidas, preco_unitario, ativo)
VALUES
 ('TL-001', 'Ladrilho Coliseu', (SELECT id FROM tb_categoria WHERE nome = 'Classico'),   '20x20', 49.90, TRUE),
 ('TL-002', 'Ladrilho Viena',   (SELECT id FROM tb_categoria WHERE nome = 'Geometrico'), '20x20', 54.90, TRUE),
 ('TL-003', 'Ladrilho Siena',   (SELECT id FROM tb_categoria WHERE nome = 'Florais'),    '20x20', 59.90, TRUE)
ON CONFLICT DO NOTHING;

-- Cliente demo
INSERT INTO tb_cliente (nome, cpf_cnpj, email, telefone, cep, cidade, uf)
VALUES ('Cliente Demo', '12345678000199', 'cliente@demo.com', '(11) 99999-0000', '01000-000', 'São Paulo', 'SP')
ON CONFLICT DO NOTHING;
