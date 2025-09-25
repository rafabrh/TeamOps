package com.rafadev.teamops.service;

import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.security.JwtService;
import com.rafadev.teamops.security.PasswordService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordService password;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordService password, JwtService jwt) {
        this.users = users;
        this.password = password;
        this.jwt = jwt;
    }

    /** Login por email (preferência se enviado) ou login. Retorna access+refresh. */
    public Tokens login(String email, String login, String rawPassword) {
        User u = (!isBlank(email) ? users.findByEmail(email) : users.findByLogin(login))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!password.matches(rawPassword, u.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        var roles = u.getRoles().stream().map(r -> "ROLE_" + r.getName()).toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", safe(u.getEmail()));
        claims.put("name", safeOr(u.getFullName(), u.getEmail()));
        claims.put("scope", String.join(" ", roles));

        String access  = jwt.generateAccessToken(u.getId().toString(), roles, claims);
        String refresh = jwt.generateRefreshToken(u.getId().toString());
        return new Tokens(access, refresh);
    }

    /** Emite novo par de tokens validando o refresh recebido. */
    public Tokens refresh(String refreshToken) {
        var parsed = jwt.validateRefresh(refreshToken); // lança JwtException se inválido
        var userId = UUID.fromString(parsed.getSubject());
        var u = users.findById(userId).orElseThrow(() -> new BadCredentialsException("Invalid refresh"));
        var roles = u.getRoles().stream().map(r -> "ROLE_" + r.getName()).toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", safe(u.getEmail()));
        claims.put("name", safeOr(u.getFullName(), u.getEmail()));
        claims.put("scope", String.join(" ", roles));

        String access  = jwt.generateAccessToken(u.getId().toString(), roles, claims);
        String refresh = jwt.generateRefreshToken(u.getId().toString()); // rotação simples
        return new Tokens(access, refresh);
    }

    public record Tokens(String accessToken, String refreshToken) {}

    private static boolean isBlank(String s){
        return s == null || s.trim().isEmpty(); }

    private static String safe(String s){
        return s == null ? "" : s; }

    private static String safeOr(String primary, String fallback){
        return isBlank(primary) ? safe(fallback) : primary; }
}
