package com.innowise.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenUtils {

    @Value("${JWT_SECRET}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (IllegalArgumentException _) {
            throw new JwtException("Invalid JWT token");
        }
    }

    public String getRoleFromClaims(Claims claims) {
        return claims.get("role", String.class);
    }

    public String getLoginFromClaims(Claims claims) {
        return claims.getSubject();
    }

    public Long getIdFromClaims(Claims claims) {
        return claims.get("id", Long.class);
    }
}
