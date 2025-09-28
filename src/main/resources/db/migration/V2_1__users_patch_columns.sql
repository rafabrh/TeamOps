-- V2_1__users_patch_columns.sql

-- full_name
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS full_name varchar(160);

-- email
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email varchar(160);

-- password_hash
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS password_hash varchar(120);

-- enabled
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS enabled boolean DEFAULT true;

-- created_at / updated_at
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

-- Tentar popular full_name a partir de 'name' (se existir) quando vazio
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='name') THEN
UPDATE users SET full_name = COALESCE(NULLIF(full_name, ''), name) WHERE full_name IS NULL OR full_name = '';
END IF;
END $$;

-- Tentar popular email a partir de 'login' (se existir) quando nulo
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='login') THEN
UPDATE users SET email = COALESCE(email, login) WHERE email IS NULL OR email = '';
END IF;
END $$;

-- Constraint UNIQUE no email (se ainda não existir)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_user_email') THEN
ALTER TABLE users ADD CONSTRAINT uk_user_email UNIQUE (email);
END IF;
END $$;

