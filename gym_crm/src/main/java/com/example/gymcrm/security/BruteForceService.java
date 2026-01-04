package com.example.gymcrm.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class BruteForceService {

    private final int maxAttempts;
    private final Duration lockDuration;
    private final Cache<String, Integer> attempts;
    private final Cache<String, Boolean> locks;

    public BruteForceService(@Value("${security.brute-force.max-attempts:3}") int maxAttempts,
                             @Value("${security.brute-force.lock-minutes:5}") int lockMinutes) {
        this.maxAttempts = maxAttempts;
        this.lockDuration = Duration.ofMinutes(lockMinutes);
        this.attempts = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15))
                .maximumSize(10_000)
                .build();
        this.locks = Caffeine.newBuilder()
                .expireAfterWrite(lockDuration)
                .maximumSize(10_000)
                .build();
    }

    public boolean isLocked(String username) {
        return locks.getIfPresent(username) != null;
    }

    public void recordFailure(String username) {
        int c = attempts.get(username, k -> 0) + 1;
        attempts.put(username, c);
        if (c >= maxAttempts) {
            locks.put(username, Boolean.TRUE);
            attempts.invalidate(username);
        }
    }

    public void reset(String username) {
        attempts.invalidate(username);
        locks.invalidate(username);
    }

    public long getLockSeconds() {
        return lockDuration.toSeconds();
    }
}
