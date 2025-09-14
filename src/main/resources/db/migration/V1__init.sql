CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS roles (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(32) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome VARCHAR(120) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    email VARCHAR(160) UNIQUE NOT NULL,
    cargo VARCHAR(80),
    login VARCHAR(80) UNIQUE NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
    );

INSERT INTO roles (id, name) VALUES
                                 ('00000000-0000-0000-0000-000000000001','ADMIN'),
                                 ('00000000-0000-0000-0000-000000000002','MANAGER'),
                                 ('00000000-0000-0000-0000-000000000003','COLLAB')
    ON CONFLICT (id) DO NOTHING;
