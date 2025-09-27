package com.rafadev.teamops.bootstrap;

import com.rafadev.teamops.domain.Role;
import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.RoleRepository;
import com.rafadev.teamops.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    public AdminBootstrap(UserRepository users, RoleRepository roles, PasswordEncoder encoder) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
    }

    @Value("${teamops.admin.login:admin}")
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
        Role rAdmin        = ensureRole("ROLE_ADMIN");
        Role rManager      = ensureRole("ROLE_MANAGER");
        Role rColaborador  = ensureRole("ROLE_COLABORADOR");

        users.findByLogin(adminLogin).ifPresentOrElse(
                u -> {
                    if (u.getRoles().stream().noneMatch(r -> "ROLE_ADMIN".equals(r.getName()))) {
                        u.getRoles().add(rAdmin);
                        users.save(u);
                    }
                },
                () -> {
                    User u = new User();
                    u.setFullName("Administrador");
                    u.setCpf("000.000.000-00");
                    u.setEmail("admin@teamops.local");
                    u.setCargo("ROLE_ADMIN");
                    u.setLogin(adminLogin);
                    u.setPasswordHash(encoder.encode(adminPassword));
                    u.setRoles(Set.of(rAdmin)); // só ROLE_ADMIN no seed
                    users.save(u);
                }
        );
    }
}

