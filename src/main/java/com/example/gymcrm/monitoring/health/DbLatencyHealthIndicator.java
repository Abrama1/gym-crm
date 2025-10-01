package com.example.gymcrm.monitoring.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DbLatencyHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public DbLatencyHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            long start = System.nanoTime();
            Integer one = jdbcTemplate.queryForObject("select 1", Integer.class);
            long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            return Health.up().withDetail("ping", one).withDetail("latencyMs", ms).build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
