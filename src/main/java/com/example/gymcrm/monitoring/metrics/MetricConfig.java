package com.example.gymcrm.monitoring.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {

    private final MeterRegistry registry;

    @PersistenceContext
    private EntityManager em;

    public MetricConfig(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    public void registerGauges() {
        registry.gauge("gym_active_users", this, MetricConfig::countActiveUsers);
    }

    private double countActiveUsers() {
        Long cnt = em.createQuery("select count(u) from User u where u.active=true", Long.class)
                .getSingleResult();
        return cnt == null ? 0.0 : cnt.doubleValue();
    }
}
