package com.rafadev.teamops.web;

import com.rafadev.teamops.service.AuthService;
import com.rafadev.teamops.web.dto.LoginRequest;
import com.rafadev.teamops.web.dto.LoginResponse;
import jakarta.validation.Valid;
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
}
