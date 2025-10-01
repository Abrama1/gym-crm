package com.example.gymcrm.monitoring.health;

import com.example.gymcrm.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class UsersHealthIndicator implements HealthIndicator {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Health health() {
        long total = ((Number) em.createQuery(
                        "select count(u) from " + User.class.getSimpleName() + " u")
                .getSingleResult()).longValue();

        long invalid = ((Number) em.createQuery(
                        "select count(u) from " + User.class.getSimpleName() + " u " +
                                "where u.username is null or u.password is null")
                .getSingleResult()).longValue();

        Status status = (invalid == 0) ? Status.UP : Status.OUT_OF_SERVICE;
        return Health.status(status)
                .withDetail("users.total", total)
                .withDetail("users.invalidCreds", invalid)
                .build();
    }
}
