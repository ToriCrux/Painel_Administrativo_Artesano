-- Extensão necessária para gerar hash bcrypt via crypt()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS tb_usuario (
    id               BIGSERIAL PRIMARY KEY,
    nome             VARCHAR(120) NOT NULL,
    email            VARCHAR(160) NOT NULL,
    password_hash    VARCHAR(255) NOT NULL,
    ativo            BOOLEAN NOT NULL DEFAULT TRUE,
    role             VARCHAR(32) NOT NULL DEFAULT 'USUARIO',
    criado_em        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em    TIMESTAMPTZ NULL,
    CONSTRAINT ck_usuario_role CHECK (role IN ('ADMINISTRADOR', 'USUARIO'))
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_usuario_email_lower
    ON tb_usuario (LOWER(email));

INSERT INTO tb_usuario (nome, email, password_hash, ativo, role)
VALUES
    ('Administrador', 'admin@exemplo.com',
     crypt('Admin@123', gen_salt('bf', 10)), TRUE, 'ADMINISTRADOR'),
    ('Maria Silva', 'maria.silva@exemplo.com',
     crypt('Senha@123', gen_salt('bf', 10)), TRUE, 'USUARIO'),
    ('João Souza', 'joao.souza@exemplo.com',
     crypt('Senha@123', gen_salt('bf', 10)), TRUE, 'USUARIO')
    ON CONFLICT DO NOTHING;
