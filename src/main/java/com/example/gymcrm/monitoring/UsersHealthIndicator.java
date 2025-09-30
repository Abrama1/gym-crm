package com.example.gymcrm.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("users")
@ConditionalOnBean(DataSource.class)
public class UsersHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbc;

    public UsersHealthIndicator(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Health health() {
        try {
            Integer cnt = jdbc.queryForObject("select count(1) from users", Integer.class);
            return Health.up()
                    .withDetail("table", "users")
                    .withDetail("count", cnt)
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("table", "users")
                    .build();
        }
    }
}
