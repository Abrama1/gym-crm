package com.example.gymcrm.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("trainingTypes")
@ConditionalOnBean(DataSource.class)
public class TrainingTypesHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbc;

    public TrainingTypesHealthIndicator(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Health health() {
        try {
            Integer cnt = jdbc.queryForObject("select count(1) from training_type", Integer.class);
            if (cnt != null && cnt > 0) {
                return Health.up()
                        .withDetail("table", "training_type")
                        .withDetail("count", cnt)
                        .build();
            }
            return Health.outOfService()
                    .withDetail("table", "training_type")
                    .withDetail("reason", "No training types present")
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("table", "training_type")
                    .build();
        }
    }
}
