package com.example.workload.repo;

import com.example.workload.entity.WorkloadSummary;
import com.example.workload.entity.WorkloadSummaryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkloadSummaryRepository extends JpaRepository<WorkloadSummary, WorkloadSummaryId> {
    List<WorkloadSummary> findByIdTrainerUsernameOrderByIdYearAscIdMonthAsc(String trainerUsername);
}
