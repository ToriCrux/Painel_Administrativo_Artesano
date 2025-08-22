-- Seed de roles padrão (idempotente)

INSERT INTO tb_role (nome)
SELECT 'USUARIO'
WHERE NOT EXISTS (SELECT 1 FROM tb_role WHERE nome = 'USUARIO');

INSERT INTO tb_role (nome)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM tb_role WHERE nome = 'ADMIN');
