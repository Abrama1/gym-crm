package com.example.gymcrm.monitoring.health;

import com.example.gymcrm.entity.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypesHealthIndicator implements HealthIndicator {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Health health() {
        long cnt = ((Number) em.createQuery(
                        "select count(t) from " + TrainingType.class.getSimpleName() + " t")
                .getSingleResult()).longValue();

        if (cnt > 0) {
            return Health.up()
                    .withDetail("trainingTypes.count", cnt)
                    .build();
        }
        return Health.down()
                .withDetail("reason", "No training types present (seed missing?)")
                .build();
    }
}
