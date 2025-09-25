package com.rafadev.teamops.service;

import com.rafadev.teamops.domain.Role;
import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.RoleRepository;
import com.rafadev.teamops.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository users, RoleRepository roles, PasswordEncoder encoder) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
    }

    // ===== DTOs =====
    public record Tokens(String accessToken, String refreshToken) {}
    public record CreatedUser(String id, String code, String email, String role) {}

    // ===== LOGIN (público) =====
    @Transactional(readOnly = true)
    public Tokens login(String tenant, String login, String password) {
        // tenta por login; se não achar, tenta por email
        Optional<User> opt = users.findByLogin(login);
        if (opt.isEmpty()) {
            opt = users.findByEmail(login.toLowerCase().trim());
        }
        User u = opt.orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        // TODO: substituir pelos JWTs reais (JwtService)
        String access = "acc-" + UUID.randomUUID();
        String refresh = "ref-" + UUID.randomUUID();
        return new Tokens(access, refresh);
    }

    // ===== REFRESH =====
    @Transactional(readOnly = true)
    public Tokens refresh(String refreshToken) {
        // TODO: validar refreshToken e emitir novos tokens reais
        String access = "acc-" + UUID.randomUUID();
        String refresh = "ref-" + UUID.randomUUID();
        return new Tokens(access, refresh);
    }

    // ===== CADASTRO PÚBLICO: sempre COLABORADOR =====
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
        u.setFullName(name.trim());          // sincroniza fullName/nome
        u.setCpf(cpf);
        u.setEmail(normEmail);
        u.setLogin(normLogin);
        u.setPasswordHash(encoder.encode(rawPassword));

        // garante ROLE_COLABORADOR
        Role colaborador = roles.findByName("ROLE_COLABORADOR").orElseGet(() -> {
            Role r = new Role();             // sem construtor com args
            r.setName("ROLE_COLABORADOR");
            return roles.save(r);
        });
        u.getRoles().add(colaborador);

        // >>> NÃO REMOVER: 'saved' é usado abaixo <<<
        User saved = users.save(u);

        return new CreatedUser(
                saved.getId().toString(),
                saved.getCode(),              // getter adicionado na entidade User
                saved.getEmail(),
                "ROLE_COLABORADOR"
        );
    }
}
