-- base de usuários/roles
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS roles (
                                     id   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(32) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS users (
                                     id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name     varchar(160) NOT NULL,
    email         varchar(160) NOT NULL UNIQUE,
    password_hash varchar(120) NOT NULL,
    enabled       boolean      NOT NULL DEFAULT true,
    created_at    timestamptz  NOT NULL DEFAULT now(),
    updated_at    timestamptz  NOT NULL DEFAULT now()
    );

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id uuid NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
    );

-- roles padrão
INSERT INTO roles (id, name) VALUES
                                 (gen_random_uuid(),'ADMIN'),
                                 (gen_random_uuid(),'MANAGER'),
                                 (gen_random_uuid(),'COLLAB')
    ON CONFLICT (name) DO NOTHING;
