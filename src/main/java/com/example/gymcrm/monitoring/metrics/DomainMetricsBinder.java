package com.example.gymcrm.monitoring.metrics;

import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class DomainMetricsBinder implements MeterBinder {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("gymcrm_users_total", this::countUsers)
                .description("Total users")
                .register(registry);

        Gauge.builder("gymcrm_users_active", this::countActiveUsers)
                .description("Active users")
                .register(registry);

        Gauge.builder("gymcrm_trainees_total", this::countTrainees)
                .description("Total trainees")
                .register(registry);

        Gauge.builder("gymcrm_trainers_total", this::countTrainers)
                .description("Total trainers")
                .register(registry);
    }

    private double countUsers() {
        return ((Number) em.createQuery(
                        "select count(u) from " + User.class.getSimpleName() + " u")
                .getSingleResult()).doubleValue();
    }

    private double countActiveUsers() {
        return ((Number) em.createQuery(
                        "select count(u) from " + User.class.getSimpleName() + " u where u.active = true")
                .getSingleResult()).doubleValue();
    }

    private double countTrainees() {
        return ((Number) em.createQuery(
                        "select count(t) from " + Trainee.class.getSimpleName() + " t")
                .getSingleResult()).doubleValue();
    }

    private double countTrainers() {
        return ((Number) em.createQuery(
                        "select count(t) from " + Trainer.class.getSimpleName() + " t")
                .getSingleResult()).doubleValue();
    }
}
