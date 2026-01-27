package com.example.workload.messaging;

import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.service.WorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class WorkloadEventListener {

    private static final Logger log = LoggerFactory.getLogger(WorkloadEventListener.class);

    private final WorkloadService service;

    public WorkloadEventListener(WorkloadService service) {
        this.service = service;
    }

    @JmsListener(destination = "${workload.queue.events}")
    public void onMessage(WorkloadEventRequest req, jakarta.jms.Message message) {
        String txId = null;
        try {
            txId = message.getStringProperty("txId");
        } catch (Exception ignore) {}

        if (txId != null) MDC.put("txId", txId);

        try {
            service.applyEvent(req);
            log.info("Consumed workload event trainer={} action={}", req.getTrainerUsername(), req.getActionType());
        } finally {
            if (txId != null) MDC.remove("txId");
        }
    }
}
