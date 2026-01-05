package com.example.gymcrm.integration;

import com.example.gymcrm.security.JwtService;
import org.springframework.stereotype.Component;

@Component
public class ServiceTokenProvider {

    private final JwtService jwt;

    public ServiceTokenProvider(JwtService jwt) {
        this.jwt = jwt;
    }

    public String createServiceToken() {
        return jwt.generate("gym-crm", "SERVICE");
    }
}
