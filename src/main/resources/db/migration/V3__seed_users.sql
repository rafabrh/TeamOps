-- garanta as roles básicas (idempotente)
INSERT INTO roles (name) VALUES ('ADMIN')   ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('MANAGER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('COLLAB')  ON CONFLICT (name) DO NOTHING;

WITH seed AS (
    SELECT * FROM (VALUES
                       ('00000000-0000-0000-0000-0000000000a1'::uuid,'Admin User','Admin User','admin@teamops.local','admin@teamops.local','administrador','000.000.000-01','$2a$10$QZz0cQ2a2Nqv7G1cHj9Qpeu0S0eSx6O5f8qjQj3n2s7e0qM5Qn8q2'),
                       ('00000000-0000-0000-0000-0000000000b1'::uuid,'Manager User','Manager User','manager@teamops.local','manager@teamops.local','gerente','000.000.000-02','$2a$10$QZz0cQ2a2Nqv7G1cHj9Qpeu0S0eSx6O5f8qjQj3n2s7e0qM5Qn8q2'),
                       ('00000000-0000-0000-0000-0000000000c1'::uuid,'Collab User','Collab User','collab@teamops.local','collab@teamops.local','colaborador','000.000.000-03','$2a$10$QZz0cQ2a2Nqv7G1cHj9Qpeu0S0eSx6O5f8qjQj3n2s7e0qM5Qn8q2')
                  ) AS v(id,nome,full_name,email,login,cargo,cpf,password_hash)
)
INSERT INTO users (id, nome, full_name, email, login, cargo, cpf, password_hash, enabled, created_at, updated_at)
SELECT s.id, s.nome, s.full_name, s.email, s.login, s.cargo, s.cpf, s.password_hash, true, now(), now()
FROM seed s
-- evita qualquer conflito (email, id, cpf, etc.)
    ON CONFLICT DO NOTHING;


-- vinculações idempotentes
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u JOIN roles r ON r.name='ADMIN'
WHERE u.email='admin@teamops.local'
    ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u JOIN roles r ON r.name='MANAGER'
WHERE u.email='manager@teamops.local'
    ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u JOIN roles r ON r.name='COLLAB'
WHERE u.email='collab@teamops.local'
    ON CONFLICT DO NOTHING;
