package com.rafadev.teamops.service;

import com.rafadev.teamops.domain.Role;
import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.RoleRepository;
import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, RoleRepository roles, PasswordEncoder encoder, JwtService jwtService) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public record Tokens(String accessToken, String refreshToken) {}
    public record CreatedUser(String id, String code, String email, String role) {}

    @Transactional(readOnly = true)
    public Tokens login(String tenant, String login, String password) {
        Optional<User> opt = users.findByLogin(login);
        if (opt.isEmpty()) {
            opt = users.findByEmail(login.toLowerCase().trim());
        }
        User u = opt.orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        var roleNames = u.getRoles().stream().map(Role::getName).toList(); // ROLE_*
        var extra = Map.<String,Object>of("email", u.getEmail(), "name", u.getFullName());

        String access  = jwtService.generateAccessToken(u.getId().toString(), roleNames, extra);
        String refresh = jwtService.generateRefreshToken(u.getId().toString());
        return new Tokens(access, refresh);
    }

    @Transactional(readOnly = true)
    public Tokens refresh(String refreshToken) {
        var parsed = jwtService.validateRefresh(refreshToken);
        var userId = UUID.fromString(parsed.getSubject());
        var u = users.findById(userId).orElseThrow();

        var roleNames = u.getRoles().stream().map(Role::getName).toList();
        var extra = Map.<String,Object>of(
                "email", u.getEmail(),
                "name",  u.getFullName()
        );

        String access  = jwtService.generateAccessToken(u.getId().toString(), roleNames, extra);
        String refresh = jwtService.generateRefreshToken(u.getId().toString());
        return new Tokens(access, refresh);
    }

    @Transactional
    public CreatedUser registerCollaborator(String name, String cpf, String email, String rawPassword, String login) {
        String normEmail = email.trim().toLowerCase();
        String normLogin = login.trim();

        if (users.findByEmail(normEmail).isPresent()) {
            throw new IllegalStateException("E-mail já cadastrado: " + normEmail);
        }
        if (users.findByLogin(normLogin).isPresent()) {
            throw new IllegalStateException("Login já cadastrado: " + normLogin);
        }
        if (users.findByCpf(cpf).isPresent()) {
            throw new IllegalStateException("CPF já cadastrado: " + cpf);
        }

        User u = new User();
        u.setFullName(name.trim());
        u.setCpf(cpf);
        u.setEmail(normEmail);
        u.setLogin(normLogin);
        u.setPasswordHash(encoder.encode(rawPassword));

        Role colaborador = roles.findByName("ROLE_COLABORADOR").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_COLABORADOR");
            return roles.save(r);
        });
        u.getRoles().add(colaborador);

        User saved = users.save(u);

        return new CreatedUser(
                saved.getId().toString(),
                saved.getCode(),
                saved.getEmail(),
                "ROLE_COLABORADOR"
        );
    }
}
