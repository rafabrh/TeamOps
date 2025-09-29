package com.rafadev.teamops.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var gac = new JwtGrantedAuthoritiesConverter();
        gac.setAuthoritiesClaimName("authorities");
        gac.setAuthorityPrefix("");
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(gac);
        return converter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter jwtConv) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/health", "/error",
                                "/v1/auth/login", "/v1/auth/register",
                                "/actuator/**",
                                "/v1/docs",
                                "/v1/swagger-ui/**",
                                "/swagger-ui/**",
                                "/v1/api-docs/**", "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/v1/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(daoAuthenticationProvider())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConv)));

        return http.build();
    }

//    @Bean
//    JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
//        byte[] keyBytes;
//        if (secret.startsWith("base64:")) {
//            keyBytes = Decoders.BASE64.decode(secret.substring("base64:".length()));
//        } else if (secret.startsWith("raw:")) {
//            keyBytes = secret.substring("raw:".length()).getBytes(StandardCharsets.UTF_8);
//        } else {
//            try { keyBytes = Decoders.BASE64.decode(secret); }
//            catch (Exception e) { keyBytes = secret.getBytes(StandardCharsets.UTF_8); }
//        }
//        var key = new SecretKeySpec(keyBytes, "HmacSHA256");
//        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
//    }
}
