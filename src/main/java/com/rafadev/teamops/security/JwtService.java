package com.rafadev.teamops.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;
    private final long accessTtlSeconds;   // ex.: 900 (15 min)
    private final long refreshTtlSeconds;  // ex.: 604800 (7 dias)
    private final String issuer;
    private final String audience;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-ttl-seconds:900}") long accessTtlSeconds,
            @Value("${security.jwt.refresh-ttl-seconds:604800}") long refreshTtlSeconds,
            @Value("${security.jwt.issuer:teamops}") String issuer,
            @Value("${security.jwt.audience:teamops-api}") String audience
    ) {
        byte[] keyBytes;

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is empty. Set security.jwt.secret or JWT_SECRET env.");
        }

        if (secret.startsWith("base64:")) {
            keyBytes = Decoders.BASE64.decode(secret.substring("base64:".length()));
        } else if (secret.startsWith("raw:")) {
            keyBytes = secret.substring("raw:".length()).getBytes(StandardCharsets.UTF_8);
        } else {
            try {
                keyBytes = Decoders.BASE64.decode(secret);
            } catch (Exception e) { // DecodingException etc -> cai para texto
                keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            }
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must be at least 32 bytes (256 bits). Current length: " + keyBytes.length +
                            " bytes. Provide a longer value, e.g., raw:<min 32 chars> or a Base64 string."
            );
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
        this.audience = audience;
    }

    public String generateAccessToken(String subject, Collection<String> roles, Map<String, Object> extraClaims) {
        Instant now = Instant.now();

        JwtBuilder b = Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claim("scope", roles == null ? "" : String.join(" ", roles));

        if (extraClaims != null) extraClaims.forEach(b::claim);

        return b.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("typ", "refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Valida refresh: issuer, audience, expiração e claim typ=refresh. */
    public ParsedJwt validateRefresh(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseClaimsJws(token);

            Claims c = jws.getBody();
            if (c.getExpiration() == null || c.getExpiration().before(new Date())) {
                throw new ExpiredJwtException(jws.getHeader(), c, "Refresh token expired");
            }
            Object typ = c.get("typ");
            if (typ == null || !"refresh".equals(typ.toString())) {
                throw new JwtException("Invalid token type");
            }
            return new ParsedJwt(c.getSubject());
        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtException("Invalid refresh token", e);
        }
    }

    /** Wrapper simples compatível com o uso no AuthService. */
    public static class ParsedJwt {
        private final String subject;
        public ParsedJwt(String subject) { this.subject = subject; }
        public String getSubject() { return subject; }
    }
}
