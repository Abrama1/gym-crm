package com.example.workload.service.impl;

import com.example.workload.dto.ActionType;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.entity.WorkloadSummary;
import com.example.workload.entity.WorkloadSummaryId;
import com.example.workload.repo.WorkloadSummaryRepository;
import com.example.workload.service.WorkloadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkloadServiceImpl implements WorkloadService {

    private final WorkloadSummaryRepository repo;

    public WorkloadServiceImpl(WorkloadSummaryRepository repo) {
        this.repo = repo;
    }

    @Override
    public void applyEvent(WorkloadEventRequest req) {
        int year = req.getTrainingDate().getYear();
        int month = req.getTrainingDate().getMonthValue();

        WorkloadSummaryId id = new WorkloadSummaryId(req.getTrainerUsername(), year, month);

        WorkloadSummary row = repo.findById(id).orElseGet(() -> {
            WorkloadSummary ws = new WorkloadSummary();
            ws.setId(id);
            ws.setTotalMinutes(0);
            return ws;
        });

        // always refresh trainer info with latest message
        row.setTrainerFirstName(req.getTrainerFirstName());
        row.setTrainerLastName(req.getTrainerLastName());
        row.setActive(Boolean.TRUE.equals(req.getActive()));

        int delta = req.getTrainingDurationMinutes() != null ? req.getTrainingDurationMinutes() : 0;
        if (req.getActionType() == ActionType.DELETE) delta = -delta;

        int updated = row.getTotalMinutes() + delta;
        if (updated < 0) updated = 0; // safety for avoiding negative totals

        row.setTotalMinutes(updated);
        repo.save(row);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkloadSummary getMonth(String trainerUsername, int year, int month) {
        WorkloadSummaryId id = new WorkloadSummaryId(trainerUsername, year, month);
        return repo.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkloadSummary> getAllMonths(String trainerUsername) {
        return repo.findByIdTrainerUsernameOrderByIdYearAscIdMonthAsc(trainerUsername);
    }
}
