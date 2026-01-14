
UPDATE tb_role SET nome = 'ROLE_USER'      WHERE LOWER(nome) = 'usuario';
UPDATE tb_role SET nome = 'ROLE_ADMIN'     WHERE LOWER(nome) = 'admin';
UPDATE tb_role SET nome = 'ROLE_OPERADOR'  WHERE LOWER(nome) = 'operador';

-- Roles no padrão Spring Security
INSERT INTO tb_role (nome) VALUES ('ROLE_USER')      ON CONFLICT DO NOTHING;
INSERT INTO tb_role (nome) VALUES ('ROLE_ADMIN')     ON CONFLICT DO NOTHING;
INSERT INTO tb_role (nome) VALUES ('ROLE_OPERADOR')  ON CONFLICT DO NOTHING;
