package com.example.gymcrm.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component("usernames")
public class UsernameUniquenessHealthIndicator implements HealthIndicator {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Health health() {
        Object[] row = em.createQuery(
                        "select count(u), count(distinct u.username) from User u", Object[].class)
                .getSingleResult();
        long total = ((Number) row[0]).longValue();
        long distinct = ((Number) row[1]).longValue();
        if (total == distinct) {
            return Health.up().withDetail("totalUsers", total).build();
        }
        return Health.down()
                .withDetail("totalUsers", total)
                .withDetail("distinctUsernames", distinct)
                .withDetail("reason", "Duplicate usernames detected")
                .build();
    }
}
