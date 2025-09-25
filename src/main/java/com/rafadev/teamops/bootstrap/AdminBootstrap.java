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

    @Override
    public void run(String... args) {
        users.findByLogin(adminLogin).ifPresentOrElse(
                u -> {},
                () -> {
                    Role adminRole = roles.findByName("ADMIN")
                            .orElseThrow(() -> new IllegalStateException("Role ADMIN não encontrada"));
                    User u = new User();
                    u.setNome("Administrador");
                    u.setCpf("000.000.000-00");
                    u.setEmail("admin@teamops.local");
                    u.setCargo("ADMIN");
                    u.setLogin(adminLogin);
                    u.setPasswordHash(encoder.encode(adminPassword));
                    u.setRoles(Set.of(adminRole));
                    users.save(u);
                }
        );
    }
}
