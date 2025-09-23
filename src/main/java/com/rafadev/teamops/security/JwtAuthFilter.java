package com.rafadev.teamops.security;

import com.rafadev.teamops.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository users;
    private final byte[] key;

    public JwtAuthFilter(UserRepository users, @Value("${security.jwt.secret}") String secret) {
        this.users = users;
        this.key = secret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String auth = req.getHeader("Authorization");
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                var claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(key))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String login = claims.getSubject();

                users.findByLogin(login).ifPresent(u -> {
                    var authorities = u.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                            .collect(Collectors.toSet());
                    var authToken = new UsernamePasswordAuthenticationToken(login, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                });
            } catch (Exception ignored) {
            }
        }
        chain.doFilter(req, res);
    }
}
