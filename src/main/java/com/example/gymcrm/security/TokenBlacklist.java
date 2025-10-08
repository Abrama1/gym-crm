package com.example.gymcrm.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TokenBlacklist {

    private final Cache<String, Boolean> revoked = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(2)) // safety window
            .maximumSize(10000)
            .build();

    public void revoke(String token) {
        revoked.put(token, Boolean.TRUE);
    }

    public boolean isRevoked(String token) {
        return revoked.getIfPresent(token) != null;
    }
}
