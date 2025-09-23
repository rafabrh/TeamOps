package com.rafadev.teamops.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;
    private final long ttlSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.ttl-seconds:3600}") long ttlSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.ttlSeconds = ttlSeconds;
    }

    /** Aceita claims de qualquer tipo (List, Set, arrays, etc). */
    public String generate(String subject, Map<String, ?> claims) {
        Instant now = Instant.now();

        var builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)));

        // adiciona cada claim individualmente (evita exigir Map<String, Object>)
        claims.forEach(builder::claim);

        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }
}
