package com.example.workload.api;

import com.example.workload.dto.MonthSummaryResponse;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.entity.WorkloadSummary;
import com.example.workload.service.WorkloadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workloads")
public class WorkloadController {

    private final WorkloadService service;

    public WorkloadController(WorkloadService service) {
        this.service = service;
    }

    // 1) Accept workload events (ADD/DELETE)
    @PostMapping("/events")
    public ResponseEntity<Void> apply(@RequestBody @Valid WorkloadEventRequest req) {
        service.applyEvent(req);
        return ResponseEntity.ok().build();
    }

    // 2) Get month summary for a trainer
    @GetMapping("/{trainerUsername}/months/{year}/{month}")
    public ResponseEntity<MonthSummaryResponse> month(
            @PathVariable String trainerUsername,
            @PathVariable int year,
            @PathVariable int month
    ) {
        WorkloadSummary ws = service.getMonth(trainerUsername, year, month);
        int total = (ws == null) ? 0 : ws.getTotalMinutes();
        return ResponseEntity.ok(new MonthSummaryResponse(trainerUsername, year, month, total));
    }

    // 3) Get all months (flat list) for a trainer (we'll later transform to Years->Months model)
    @GetMapping("/{trainerUsername}")
    public ResponseEntity<List<WorkloadSummary>> all(@PathVariable String trainerUsername) {
        return ResponseEntity.ok(service.getAllMonths(trainerUsername));
    }
}
