package com.example.gymcrm.integration;

import com.example.gymcrm.dto.WorkloadEventRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class WorkloadEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(WorkloadEventPublisher.class);

    private final JmsTemplate jmsTemplate;
    private final String eventsQueue;

    public WorkloadEventPublisher(JmsTemplate jmsTemplate,
                                  @Value("${workload.queue.events}") String eventsQueue) {
        this.jmsTemplate = jmsTemplate;
        this.eventsQueue = eventsQueue;
    }

    public void publish(WorkloadEventRequest req) {
        String txId = MDC.get("txId");

        jmsTemplate.convertAndSend(eventsQueue, req, msg -> {
            if (txId != null) msg.setStringProperty("txId", txId);
            if (req.getActionType() != null) msg.setStringProperty("actionType", req.getActionType());
            if (req.getTrainerUsername() != null) msg.setStringProperty("trainerUsername", req.getTrainerUsername());
            return msg;
        });

        log.info("Published workload event trainer={} action={}", req.getTrainerUsername(), req.getActionType());
    }
}
