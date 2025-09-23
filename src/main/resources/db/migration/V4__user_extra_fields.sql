-- acrescenta CPF e cargo (requisito explícito do enunciado)
ALTER TABLE users ADD COLUMN IF NOT EXISTS cpf   varchar(14);
ALTER TABLE users ADD COLUMN IF NOT EXISTS cargo varchar(80) NOT NULL DEFAULT 'colaborador';

-- índice/unique para CPF
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_user_cpf') THEN
ALTER TABLE users ADD CONSTRAINT uk_user_cpf UNIQUE (cpf);
END IF;
END$$;

-- preencher seeds com dados demonstrativos
UPDATE users SET cpf='000.000.000-01', cargo='administrador' WHERE email='admin@teamops.local';
UPDATE users SET cpf='000.000.000-02', cargo='gerente'       WHERE email='manager@teamops.local';
UPDATE users SET cpf='000.000.000-03', cargo='colaborador'   WHERE email='collab@teamops.local';
