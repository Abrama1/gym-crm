package com.example.workload.service;

import com.example.workload.dto.TrainerWorkloadResponse;
import com.example.workload.dto.WorkloadEventRequest;

public interface WorkloadService {

    void applyEvent(WorkloadEventRequest req);

    int getMonthMinutes(String trainerUsername, int year, int month);

    TrainerWorkloadResponse getTrainerWorkload(String trainerUsername);
}
