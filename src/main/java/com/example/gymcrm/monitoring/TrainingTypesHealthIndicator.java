package com.example.gymcrm.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component("trainingTypes")
public class TrainingTypesHealthIndicator implements HealthIndicator {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Health health() {
        Long cnt = em.createQuery("select count(t) from TrainingType t", Long.class)
                .getSingleResult();
        if (cnt != null && cnt > 0) {
            return Health.up().withDetail("count", cnt).build();
        }
        return Health.status("OUT_OF_SERVICE")
                .withDetail("reason", "No training types configured")
                .build();
    }
}
