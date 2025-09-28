package com.rafadev.teamops.bootstrap;

import com.rafadev.teamops.domain.Role;
import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.RoleRepository;
import com.rafadev.teamops.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@ConditionalOnProperty(value = "teamops.bootstrap.enabled", havingValue = "true", matchIfMissing = false)
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    public AdminBootstrap(UserRepository users, RoleRepository roles, PasswordEncoder encoder) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
    }

    @Value("${teamops.admin.email:admin@teamops.local}")
    private String adminEmail;

    @Value("${teamops.admin.login:admin@teamops.local}")
    private String adminLogin;

    @Value("${teamops.admin.password:admin}")
    private String adminPassword;

    private Role ensureRole(String name) {
        return roles.findByName(name).orElseGet(() -> {
            Role r = new Role();
            r.setName(name);
            return roles.save(r);
        });
    }

    @Override
    public void run(String... args) {
        // Alinhar com o seed SQL: ADMIN, MANAGER, COLLAB
        Role rAdmin   = ensureRole("ADMIN");
        ensureRole("MANAGER");
        ensureRole("COLLAB");

        // Idempotente por e-mail (evita conflito com Flyway seed)
        users.findByEmail(adminEmail).ifPresentOrElse(
                u -> {
                    if (u.getRoles().stream().noneMatch(r -> "ADMIN".equals(r.getName()))) {
                        u.getRoles().add(rAdmin);
                        users.save(u);
                    }
                },
                () -> {
                    User u = new User();
                    u.setFullName("Administrador");
                    u.setCpf("000.000.000-00");
                    u.setEmail(adminEmail);
                    u.setCargo("administrador");
                    u.setLogin(adminLogin);
                    u.setPasswordHash(encoder.encode(adminPassword));
                    u.setRoles(Set.of(rAdmin)); // só ADMIN no seed
                    users.save(u);
                }
        );
    }
}
