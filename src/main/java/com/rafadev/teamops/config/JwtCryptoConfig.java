package com.rafadev.teamops.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtCryptoConfig {

    @Value("${security.jwt.secret}")
    private String secret; // aceita Base64 (32 bytes) OU "raw:<min 32 chars>"

    private SecretKey buildHmacKey(String value) {
        // Se vier como raw:... usa texto puro; caso contrário, tenta Base64
        byte[] keyBytes;
        if (value.startsWith("raw:")) {
            keyBytes = value.substring(4).getBytes(StandardCharsets.UTF_8);
        } else {
            keyBytes = java.util.Base64.getDecoder().decode(value);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes");
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey key = buildHmacKey(secret);
        var jwkSource = new com.nimbusds.jose.jwk.source.ImmutableSecret<>(key.getEncoded());
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = buildHmacKey(secret);
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
