package com.rafadev.teamops.web;

import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.security.JwtService;
import com.rafadev.teamops.web.dto.LoginRequest;
import com.rafadev.teamops.web.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final UserRepository users;
    private final JwtService jwt;

    public AuthController(UserRepository users, JwtService jwt) {
        this.users = users; this.jwt = jwt;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest in) {
        var u = users.findByEmail(in.email()).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        // Validação da senha é feita no Security/JwtAuthFilter em muitos projetos.
        // Se você validar aqui, injete PasswordService e faça password.matches(in.password(), u.getPasswordHash())

        var roles = u.getRoles().stream().map(r -> "ROLE_" + r.getName()).toList();
        String access  = jwt.generateAccessToken(u.getId().toString(), roles, Map.of("email", u.getEmail(), "name", u.getFullName()));
        String refresh = jwt.generateRefreshToken(u.getId().toString());
        return new LoginResponse(access, refresh);
    }

    // --- NOVO: refresh ---
    public record RefreshRequest(String refreshToken){}
    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody RefreshRequest in){
        var refreshJwt = jwt.validateRefresh(in.refreshToken());
        var userId = UUID.fromString(refreshJwt.getSubject());
        var u = users.findById(userId).orElseThrow(() -> new BadCredentialsException("Invalid refresh"));

        var roles = u.getRoles().stream().map(r -> "ROLE_" + r.getName()).toList();
        String access  = jwt.generateAccessToken(u.getId().toString(), roles, Map.of("email", u.getEmail(), "name", u.getFullName()));
        String refresh = jwt.generateRefreshToken(u.getId().toString()); // rotação simples
        return new LoginResponse(access, refresh);
    }

    // --- NOVO: me ---
    public record MeResponse(String userId, String email, String name, String[] roles){}
    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Jwt jwt){
        String[] roles = ((String) jwt.getClaims().getOrDefault("scope","")).split(" ");
        return new MeResponse(jwt.getSubject(), (String) jwt.getClaims().get("email"), (String) jwt.getClaims().get("name"), roles);
    }
}
