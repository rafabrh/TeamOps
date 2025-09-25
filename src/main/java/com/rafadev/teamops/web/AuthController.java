package com.rafadev.teamops.web;

import com.rafadev.teamops.service.AuthService;
import com.rafadev.teamops.web.dto.LoginRequest;
import com.rafadev.teamops.web.dto.LoginResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    // ===== LOGIN (público) =====
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest in) {
        var tokens = auth.login(null, in.login(), in.password());
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
    }

    public record RefreshRequest(String refreshToken) {}

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody RefreshRequest in) {
        var tokens = auth.refresh(in.refreshToken());
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
    }

    public record MeResponse(String userId, String email, String name, String[] roles) {}

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Jwt jwt){
        String[] roles = ((String) jwt.getClaims().getOrDefault("scope","")).split(" ");
        return new MeResponse(
                jwt.getSubject(),
                (String) jwt.getClaims().get("email"),
                (String) jwt.getClaims().get("name"),
                roles
        );
    }

    // ===== CADASTRO (público) -> sempre cria COLABORADOR =====
    public record RegisterRequest(
            @NotBlank String name,
            @NotBlank String cpf,                      // exigido pela entidade
            @Email @NotBlank String email,
            @NotBlank String password,
            String login                                // opcional; se nulo, usaremos o email
    ) {}

    public record RegisterResponse(String id, String code, String email, String role) {}

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest in) {
        var created = auth.registerCollaborator(
                in.name(), in.cpf(), in.email(), in.password(),
                in.login() == null || in.login().isBlank() ? in.email() : in.login()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(created.id(), created.code(), created.email(), created.role()));
    }
}
