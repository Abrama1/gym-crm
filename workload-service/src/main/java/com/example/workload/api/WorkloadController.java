package com.example.workload.api;

import com.example.workload.dto.MonthSummaryResponse;
import com.example.workload.dto.TrainerWorkloadResponse;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.service.WorkloadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workloads")
public class WorkloadController {

    private final WorkloadService service;

    public WorkloadController(WorkloadService service) {
        this.service = service;
    }

    @PostMapping("/events")
    public ResponseEntity<Void> apply(@RequestBody @Valid WorkloadEventRequest req) {
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
