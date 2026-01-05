package com.example.gymcrm.integration;

import com.example.gymcrm.dto.WorkloadEventRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WorkloadServiceClient {

    private static final Logger log = LoggerFactory.getLogger(WorkloadServiceClient.class);

    private final WebClient webClient;
    private final String baseUrl;
    private final ServiceTokenProvider tokenProvider;

    public WorkloadServiceClient(
            WebClient.Builder loadBalancedWebClientBuilder,
            @Value("${workload.service.base-url}") String baseUrl,
            ServiceTokenProvider tokenProvider
    ) {
        this.webClient = loadBalancedWebClientBuilder.build();
        this.baseUrl = baseUrl;
        this.tokenProvider = tokenProvider;
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void sendEvent(WorkloadEventRequest req) {
        String txId = MDC.get("txId");
        String token = tokenProvider.createServiceToken();

        webClient.post()
                .uri(baseUrl + "/api/workloads/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("X-Transaction-Id", txId != null ? txId : "")
                .bodyValue(req)
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("Sent workload event trainer={} action={}", req.getTrainerUsername(), req.getActionType());
    }

    @SuppressWarnings("unused")
    private void fallback(WorkloadEventRequest req, Throwable ex) {
        // don't break main business flow
        log.warn("Workload-service call failed; event skipped. trainer={} action={} reason={}",
                req.getTrainerUsername(), req.getActionType(), ex.toString());
    }
}
