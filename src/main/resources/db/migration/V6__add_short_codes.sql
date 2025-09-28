-- V6__add_short_codes.sql (seguro)

ALTER TABLE public.users    ADD COLUMN IF NOT EXISTS code varchar(16);
ALTER TABLE public.projects ADD COLUMN IF NOT EXISTS code varchar(16);
ALTER TABLE public.teams    ADD COLUMN IF NOT EXISTS code varchar(16);

CREATE UNIQUE INDEX IF NOT EXISTS uk_users_code_idx
    ON public.users(code) WHERE code IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_projects_code_idx
    ON public.projects(code) WHERE code IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_teams_code_idx
    ON public.teams(code) WHERE code IS NOT NULL;

UPDATE public.users
SET code = 'USR-' || UPPER(SUBSTRING(MD5(RANDOM()::text) FROM 1 FOR 6))
WHERE code IS NULL;

UPDATE public.projects
SET code = 'PRJ-' || UPPER(SUBSTRING(MD5(RANDOM()::text) FROM 1 FOR 6))
WHERE code IS NULL;

UPDATE public.teams
SET code = 'TEAM-' || UPPER(SUBSTRING(MD5(RANDOM()::text) FROM 1 FOR 6))
WHERE code IS NULL;

