-- ----------------------
-- SEED MÍNIMO DE DADOS (Modelo ManyToMany)
-- ----------------------

-- CATEGORIAS
INSERT INTO tb_categoria (nome, ativo)
VALUES
 ('Clássico', TRUE),
 ('Geométrico', TRUE),
 ('Florais', TRUE)
ON CONFLICT DO NOTHING;

-- SUBCATEGORIAS
INSERT INTO tb_subcategoria (nome, categoria_id, ativo)
VALUES
 ('Coleção Tradicional', (SELECT id FROM tb_categoria WHERE nome = 'Clássico'), TRUE),
 ('Coleção Moderna', (SELECT id FROM tb_categoria WHERE nome = 'Geométrico'), TRUE),
 ('Coleção Romântica', (SELECT id FROM tb_categoria WHERE nome = 'Florais'), TRUE)
ON CONFLICT DO NOTHING;

-- ITENS DE CATEGORIA
INSERT INTO tb_item_categoria (nome, subcategoria_id, ativo)
VALUES
 ('Ladrilhos Antigos', (SELECT id FROM tb_subcategoria WHERE nome = 'Coleção Tradicional'), TRUE),
 ('Ladrilhos Contemporâneos', (SELECT id FROM tb_subcategoria WHERE nome = 'Coleção Moderna'), TRUE),
 ('Ladrilhos Florais', (SELECT id FROM tb_subcategoria WHERE nome = 'Coleção Romântica'), TRUE)
ON CONFLICT DO NOTHING;

-- PRODUTOS (sem item_categoria_id)
INSERT INTO tb_produto (codigo, nome, medidas, preco_unitario, descricao, ativo)
VALUES
 ('TL-001', 'Ladrilho Coliseu', '20x20', 49.90, 'Ladrilho hidráulico estilo clássico com acabamento fosco.', TRUE),
 ('TL-002', 'Ladrilho Viena', '20x20', 54.90, 'Ladrilho com padrão geométrico sofisticado.', TRUE),
 ('TL-003', 'Ladrilho Siena', '20x20', 59.90, 'Ladrilho floral com tons suaves e acabamento artesanal.', TRUE)
ON CONFLICT DO NOTHING;

-- RELAÇÃO PRODUTO x ITEM_CATEGORIA (nova tabela)
INSERT INTO tb_produto_item_categoria (produto_id, item_categoria_id)
SELECT p.id, i.id
FROM tb_produto p
JOIN tb_item_categoria i
  ON ( (p.codigo = 'TL-001' AND i.nome = 'Ladrilhos Antigos')
    OR (p.codigo = 'TL-002' AND i.nome = 'Ladrilhos Contemporâneos')
    OR (p.codigo = 'TL-003' AND i.nome = 'Ladrilhos Florais') )
ON CONFLICT DO NOTHING;

-- CORES
INSERT INTO tb_cor (nome, hex, ativo)
VALUES
 ('Branco', '#FFFFFF', TRUE),
 ('Preto', '#000000', TRUE),
 ('Cinza', '#808080', TRUE)
ON CONFLICT DO NOTHING;

-- RELAÇÃO PRODUTO x COR
INSERT INTO tb_produto_cor (produto_id, cor_id)
SELECT p.id, c.id
FROM tb_produto p
JOIN tb_cor c ON c.nome IN ('Branco', 'Cinza')
WHERE p.codigo IN ('TL-001', 'TL-002', 'TL-003')
ON CONFLICT DO NOTHING;

-- CAMPOS DE EXIBIÇÃO PADRÃO (caso não existam)
INSERT INTO tb_campo_exibicao_padrao (nome, visivel_padrao)
VALUES
 ('codigo', true),
 ('nome', true),
 ('descricao', true),
 ('precoUnitario', true),
 ('ativo', true)
ON CONFLICT DO NOTHING;
