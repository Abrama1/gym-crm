package com.example.workload.service;

import com.example.workload.dto.TrainerWorkloadResponse;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.entity.WorkloadSummary;

import java.util.List;

public interface WorkloadService {
    void applyEvent(WorkloadEventRequest req);
    WorkloadSummary getMonth(String trainerUsername, int year, int month);
    List<WorkloadSummary> getAllMonths(String trainerUsername);
    TrainerWorkloadResponse getTrainerWorkload(String trainerUsername);
}
