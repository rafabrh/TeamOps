-- V6__add_short_codes.sql
-- Adiciona colunas 'code' e garante valor + unicidade para projects, users e teams

-- =========================
-- PROJECTS
-- =========================
DO $$
BEGIN
  -- coluna 'code'
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'projects' AND column_name = 'code'
  ) THEN
    EXECUTE 'ALTER TABLE public.projects ADD COLUMN code VARCHAR(16)';
END IF;
END$$;

-- backfill (somente onde ainda está NULL)
UPDATE public.projects
SET code = 'PRJ-' || UPPER(SUBSTRING(MD5(RANDOM()::text) FROM 1 FOR 6))
WHERE code IS NULL;

-- índice único (substitui a constraint UNIQUE nomeada)
CREATE UNIQUE INDEX IF NOT EXISTS uk_projects_code_idx ON public.projects(code);

-- marcar NOT NULL (só vai passar se já estiver tudo preenchido)
ALTER TABLE public.projects ALTER COLUMN code SET NOT NULL;

-- =========================
-- USERS
-- =========================
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'users' AND column_name = 'code'
  ) THEN
    EXECUTE 'ALTER TABLE public.users ADD COLUMN code VARCHAR(16)';
END IF;
END$$;

UPDATE public.users
SET code = 'USR-' || UPPER(SUBSTRING(MD5(RANDOM()::text) FROM 1 FOR 6))
WHERE code IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_users_code_idx ON public.users(code);
ALTER TABLE public.users ALTER COLUMN code SET NOT NULL;

-- =========================
-- TEAMS (caso exista a tabela)
-- =========================
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'public' AND table_name = 'teams'
  ) THEN
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = 'public' AND table_name = 'teams' AND column_name = 'code'
    ) THEN
      EXECUTE 'ALTER TABLE public.teams ADD COLUMN code VARCHAR(16)';
END IF;

UPDATE public.teams
SET code = 'TEAM-' || UPPER(SUBSTRING(MD5(RANDOM()::text) FROM 1 FOR 6))
WHERE code IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_teams_code_idx ON public.teams(code);
ALTER TABLE public.teams ALTER COLUMN code SET NOT NULL;
END IF;
END$$;
-- V6__add_short_codes.sql
-- Adiciona colunas 'code' e garante valor + unicidade para projects, users e teams

-- =========================
-- PROJECTS
-- =========================
DO $$
BEGIN
  -- coluna 'code'
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'projects' AND column_name = 'code'
  ) THEN
    EXECUTE 'ALTER TABLE public.projects ADD COLUMN code VARCHAR(16)';
END IF;
END$$;

-- backfill (somente onde ainda está NULL)
UPDATE public.projects
SET code = 'PRJ-' || UPPER(SUBSTRING(MD5(RANDOM()::text) FROM 1 FOR 6))
WHERE code IS NULL;

-- índice único (substitui a constraint UNIQUE nomeada)
CREATE UNIQUE INDEX IF NOT EXISTS uk_projects_code_idx ON public.projects(code);

-- marcar NOT NULL (só vai passar se já estiver tudo preenchido)
ALTER TABLE public.projects ALTER COLUMN code SET NOT NULL;

-- =========================
-- USERS
-- =========================
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'users' AND column_name = 'code'
  ) THEN
    EXECUTE 'ALTER TABLE public.users ADD COLUMN code VARCHAR(16)';
END IF;
END$$;

UPDATE public.users
SET code = 'USR-' || UPPER(SUBSTRING(MD5(RANDOM()::text) FROM 1 FOR 6))
WHERE code IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_users_code_idx ON public.users(code);
ALTER TABLE public.users ALTER COLUMN code SET NOT NULL;

-- =========================
-- TEAMS (caso exista a tabela)
-- =========================
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'public' AND table_name = 'teams'
  ) THEN
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = 'public' AND table_name = 'teams' AND column_name = 'code'
    ) THEN
      EXECUTE 'ALTER TABLE public.teams ADD COLUMN code VARCHAR(16)';
END IF;

UPDATE public.teams
SET code = 'TEAM-' || UPPER(SUBSTRING(MD5(RANDOM()::text) FROM 1 FOR 6))
WHERE code IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_teams_code_idx ON public.teams(code);
ALTER TABLE public.teams ALTER COLUMN code SET NOT NULL;
END IF;
END$$;
