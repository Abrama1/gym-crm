package com.example.gymcrm.integration.workload;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WorkloadNotifier {
    private static final Logger log = LoggerFactory.getLogger(WorkloadNotifier.class);

    private final WorkloadClient client;

    public WorkloadNotifier(WorkloadClient client) {
        this.client = client;
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void notify(WorkloadEventRequest req) {
        client.applyEvent(req);
        log.info("Workload event sent: trainer={} action={} date={} minutes={}",
                req.getTrainerUsername(), req.getActionType(), req.getTrainingDate(), req.getTrainingDurationMinutes());
    }

    @SuppressWarnings("unused")
    public void fallback(WorkloadEventRequest req, Throwable ex) {
        log.warn("Workload-service unavailable. Skipping event. trainer={} action={} reason={}",
                req.getTrainerUsername(), req.getActionType(), ex.toString());
    }
}
