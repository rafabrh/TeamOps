-- ATENÇÃO: substitua os hashes pelos gerados no seu ambiente (BCrypt)
-- password: admin123 / manager123 / collab123 (exemplos)
INSERT INTO users (id, full_name, email, password_hash, enabled, created_at, updated_at)
VALUES
    ('00000000-0000-0000-0000-0000000000a1','Admin User','admin@teamops.local','$2a$10$QZz0cQ2a2Nqv7G1cHj9Qpeu0S0eSx6O5f8qjQj3n2s7e0qM5Qn8q2',true, now(), now()),
    ('00000000-0000-0000-0000-0000000000b1','Manager User','manager@teamops.local','$2a$10$QZz0cQ2a2Nqv7G1cHj9Qpeu0S0eSx6O5f8qjQj3n2s7e0qM5Qn8q2',true, now(), now()),
    ('00000000-0000-0000-0000-0000000000c1','Collab User','collab@teamops.local','$2a$10$QZz0cQ2a2Nqv7G1cHj9Qpeu0S0eSx6O5f8qjQj3n2s7e0qM5Qn8q2',true, now(), now())
    ON CONFLICT (email) DO NOTHING;

-- vincular perfis
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name='ADMIN' WHERE u.email='admin@teamops.local'
    ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name='MANAGER' WHERE u.email='manager@teamops.local'
    ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name='COLLAB' WHERE u.email='collab@teamops.local'
    ON CONFLICT DO NOTHING;
