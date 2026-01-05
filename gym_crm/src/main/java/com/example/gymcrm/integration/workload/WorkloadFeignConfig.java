package com.example.gymcrm.integration.workload;

import com.example.gymcrm.security.JwtService;
import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

public class WorkloadFeignConfig {

    @Bean
    public RequestInterceptor workloadAuthAndTxInterceptor(JwtService jwtService) {
        return template -> {
            // Service-to-service token
            String token = jwtService.generate("gym-crm", "SERVICE");
            template.header("Authorization", "Bearer " + token);

            // Tx propagation
            String txId = MDC.get("txId");
            if (txId != null && !txId.isBlank()) {
                template.header("X-Transaction-Id", txId);
            }
        };
    }
}
