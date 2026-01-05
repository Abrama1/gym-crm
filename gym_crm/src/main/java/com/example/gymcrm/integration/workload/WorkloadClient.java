package com.example.gymcrm.integration.workload;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "workload-service", path = "/api/workloads")
public interface WorkloadClient {

    @PostMapping("/events")
    void applyEvent(@RequestBody WorkloadEventRequest req);
}
