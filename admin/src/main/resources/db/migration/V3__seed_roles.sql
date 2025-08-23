-- Seed de roles padrão (idempotente)

INSERT INTO tb_role (nome)
SELECT 'USUARIO'
WHERE NOT EXISTS (SELECT 1 FROM tb_role WHERE nome = 'USUARIO');

INSERT INTO tb_role (nome)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM tb_role WHERE nome = 'ADMIN');

MERGE INTO tb_role (nome) KEY (nome) VALUES ('ROLE_USER');
MERGE INTO tb_role (nome) KEY (nome) VALUES ('ROLE_ADMIN');
MERGE INTO tb_role (nome) KEY (nome) VALUES ('ROLE_OPERADOR');
