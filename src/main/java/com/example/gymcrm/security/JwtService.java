package com.example.gymcrm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {

    private final SecretKey key;
    private final String issuer;
    private final long ttlMinutes;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer:gym-crm}") String issuer,
            @Value("${security.jwt.ttl-minutes:60}") long ttlMinutes
    ) {
        // make sure the secret is at least 256 bits for HS256
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

    public String generate(String username, String role) {
        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plusSeconds(ttlMinutes * 60));

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(username)
                .addClaims(Map.of("role", role))
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Validate issuer explicitly to be safe
        if (claims.getIssuer() == null || !claims.getIssuer().equals(issuer)) {
            throw new IllegalStateException("Invalid JWT issuer");
        }
        return claims;
    }
}
