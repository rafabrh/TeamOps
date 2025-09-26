package com.rafadev.teamops.config;

import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
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
    private String secret;

    private SecretKey buildHmacKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("JWT secret is empty. Set security.jwt.secret or JWT_SECRET env.");
        }
        byte[] keyBytes;
        if (value.startsWith("base64:")) {
            keyBytes = Decoders.BASE64.decode(value.substring("base64:".length()));
        } else if (value.startsWith("raw:")) {
            keyBytes = value.substring("raw:".length()).getBytes(StandardCharsets.UTF_8);
        } else {
            // tenta Base64; se não der, usa UTF-8 cru (igual ao JwtService)
            try {
                keyBytes = Decoders.BASE64.decode(value);
            } catch (Exception e) {
                keyBytes = value.getBytes(StandardCharsets.UTF_8);
            }
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must be at least 32 bytes (256 bits). Current length: " + keyBytes.length
            );
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
        // declara HS256 explicitamente
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
