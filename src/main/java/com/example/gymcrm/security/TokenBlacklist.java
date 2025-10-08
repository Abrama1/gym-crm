package com.example.gymcrm.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TokenBlacklist {

    private final Cache<String, Boolean> revoked = Caffeine.newBuilder()
            // tokens are short-lived; keep a small safety window
            .expireAfterWrite(Duration.ofHours(2))
            .maximumSize(10_000)
            .build();

    public void revoke(String token) {
        revoked.put(token, Boolean.TRUE);
    }

    public boolean isRevoked(String token) {
        return revoked.getIfPresent(token) != null;
    }
}
