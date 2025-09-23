package com.rafadev.teamops.service;

import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.security.JwtService;
import com.rafadev.teamops.security.PasswordService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordService password;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordService password, JwtService jwt) {
        this.users = users; this.password = password; this.jwt = jwt;
    }

    public Tokens login(String email, String rawPassword) {
        User u = users.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!u.isEnabled() || !password.matches(rawPassword, u.getPasswordHash()))
            throw new BadCredentialsException("Invalid credentials");

        var roles = u.getRoles().stream().map(r -> "ROLE_" + r.getName()).toList();
        String access = jwt.generateAccessToken(u.getId().toString(), roles,
                Map.of("email", u.getEmail(), "name", u.getFullName()));
        String refresh = jwt.generateRefreshToken(u.getId().toString());
        return new Tokens(access, refresh);
    }

    public Tokens refresh(String refreshToken) {
        var jwtRefresh = jwt.validateRefresh(refreshToken);
        var userId = UUID.fromString(jwtRefresh.getSubject());
        var u = users.findById(userId).orElseThrow(() -> new BadCredentialsException("Invalid refresh"));
        var roles = u.getRoles().stream().map(r -> "ROLE_" + r.getName()).toList();
        String access = jwt.generateAccessToken(u.getId().toString(), roles,
                Map.of("email", u.getEmail(), "name", u.getFullName()));
        String newRefresh = jwt.generateRefreshToken(u.getId().toString()); // rotação simples
        return new Tokens(access, newRefresh);
    }

    public record Tokens(String accessToken, String refreshToken) {}
}
