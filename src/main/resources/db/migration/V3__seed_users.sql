-- V3__seed_users.sql (revisado)
-- Seeds iniciais compatíveis com schema legado que possui 'nome' (NOT NULL) e 'login'.

-- Ajuste as senhas BCrypt abaixo se desejar. No mínimo 60 chars (BCrypt).
-- Exemplo: todos usam o mesmo hash de placeholder — troque depois!
-- Senhas de exemplo: admin123 / manager123 / collab123
-- Gere seus hashes reais e substitua.
WITH seed AS (
    SELECT
        '00000000-0000-0000-0000-0000000000a1'::uuid AS id,
            'Admin User'                              AS nome,
        'Admin User'                              AS full_name,
        'admin@teamops.local'                     AS email,
        'admin@teamops.local'                     AS login,
        'administrador'                           AS cargo,
        '000.000.000-01'                          AS cpf,
        '$2a$10$QZz0cQ2a2Nqv7G1cHj9Qpeu0S0eSx6O5f8qjQj3n2s7e0qM5Qn8q2' AS password_hash
    UNION ALL
    SELECT
        '00000000-0000-0000-0000-0000000000b1',
        'Manager User',
        'Manager User',
        'manager@teamops.local',
        'manager@teamops.local',
        'gerente',
        '000.000.000-02',
        '$2a$10$QZz0cQ2a2Nqv7G1cHj9Qpeu0S0eSx6O5f8qjQj3n2s7e0qM5Qn8q2'
    UNION ALL
    SELECT
        '00000000-0000-0000-0000-0000000000c1',
        'Collab User',
        'Collab User',
        'collab@teamops.local',
        'collab@teamops.local',
        'colaborador',
        '000.000.000-03',
        '$2a$10$QZz0cQ2a2Nqv7G1cHj9Qpeu0S0eSx6O5f8qjQj3n2s7e0qM5Qn8q2'
)
INSERT INTO users (id, nome, full_name, email, login, cargo, cpf, password_hash, enabled, created_at, updated_at)
SELECT s.id, s.nome, s.full_name, s.email, s.login, s.cargo, s.cpf, s.password_hash, true, now(), now()
FROM seed s
    ON CONFLICT (email) DO NOTHING;

-- vincula roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name='ADMIN' WHERE u.email='admin@teamops.local'
    ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name='MANAGER' WHERE u.email='manager@teamops.local'
    ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name='COLLAB' WHERE u.email='collab@teamops.local'
    ON CONFLICT DO NOTHING;
