package com.example.workload.api;

import com.example.workload.dto.MonthSummaryResponse;
import com.example.workload.dto.TrainerWorkloadResponse;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.service.WorkloadService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workloads")
public class WorkloadController {

    private static final Logger log = LoggerFactory.getLogger(WorkloadController.class);

    private final WorkloadService service;

    public WorkloadController(WorkloadService service) {
        this.service = service;
    }

    @PostMapping("/events")
    public ResponseEntity<Void> apply(@RequestBody @Valid WorkloadEventRequest req) {
        log.info("tx={} endpoint=POST /api/workloads/events trainer={} action={}",
                MDC.get("txId"), req.getTrainerUsername(), req.getActionType());
        service.applyEvent(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{trainerUsername}/months/{year}/{month}")
    public ResponseEntity<MonthSummaryResponse> month(
            @PathVariable String trainerUsername,
            @PathVariable int year,
            @PathVariable int month
    ) {
        int total = service.getMonthMinutes(trainerUsername, year, month);
        return ResponseEntity.ok(new MonthSummaryResponse(trainerUsername, year, month, total));
    }

    @GetMapping("/{trainerUsername}")
    public ResponseEntity<TrainerWorkloadResponse> summary(@PathVariable String trainerUsername) {
        return ResponseEntity.ok(service.getTrainerWorkload(trainerUsername));
    }
}
