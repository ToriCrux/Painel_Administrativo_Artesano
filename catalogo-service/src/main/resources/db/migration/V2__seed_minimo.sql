-- CAMPOS DE EXIBIÇÃO PADRÃO (caso não existam)
INSERT INTO tb_campo_exibicao_padrao (nome, visivel_padrao)
VALUES
 ('codigo', true),
 ('nome', true),
 ('descricao', true),
 ('precoUnitario', true),
 ('ativo', true)
ON CONFLICT DO NOTHING;
