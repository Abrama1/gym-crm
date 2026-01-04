package com.example.workload.service.impl;

import com.example.workload.dto.ActionType;
import com.example.workload.dto.TrainerWorkloadResponse;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.entity.WorkloadSummary;
import com.example.workload.entity.WorkloadSummaryId;
import com.example.workload.repo.WorkloadSummaryRepository;
import com.example.workload.service.WorkloadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

        // refresh trainer info
        row.setTrainerFirstName(req.getTrainerFirstName());
        row.setTrainerLastName(req.getTrainerLastName());
        row.setActive(Boolean.TRUE.equals(req.getActive()));

        int delta = req.getTrainingDurationMinutes() != null ? req.getTrainingDurationMinutes() : 0;
        if (req.getActionType() == ActionType.DELETE) delta = -delta;

        int updated = row.getTotalMinutes() + delta;
        if (updated < 0) updated = 0;

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

    @Override
    @Transactional(readOnly = true)
    public TrainerWorkloadResponse getTrainerWorkload(String trainerUsername) {
        List<WorkloadSummary> rows = getAllMonths(trainerUsername);

        TrainerWorkloadResponse res = new TrainerWorkloadResponse();
        res.setTrainerUsername(trainerUsername);

        if (rows.isEmpty()) {
            // unknown trainer (no data yet)
            res.setTrainerFirstName(null);
            res.setTrainerLastName(null);
            res.setActive(false);
            res.setYears(List.of());
            return res;
        }

        // take identity fields from latest row (last element because ordered)
        WorkloadSummary last = rows.get(rows.size() - 1);
        res.setTrainerFirstName(last.getTrainerFirstName());
        res.setTrainerLastName(last.getTrainerLastName());
        res.setActive(last.isActive());

        // group by year
        Map<Integer, List<WorkloadSummary>> byYear = new LinkedHashMap<>();
        for (WorkloadSummary r : rows) {
            byYear.computeIfAbsent(r.getId().getYear(), y -> new ArrayList<>()).add(r);
        }

        List<TrainerWorkloadResponse.YearSummary> years = new ArrayList<>();
        for (var entry : byYear.entrySet()) {
            int year = entry.getKey();
            var yearDto = new TrainerWorkloadResponse.YearSummary(year);

            // months inside year are already ordered by repository method, but keeps it safe:
            entry.getValue().sort(Comparator.comparingInt(o -> o.getId().getMonth()));

            List<TrainerWorkloadResponse.MonthSummary> months = new ArrayList<>();
            for (WorkloadSummary r : entry.getValue()) {
                months.add(new TrainerWorkloadResponse.MonthSummary(
                        r.getId().getMonth(),
                        r.getTotalMinutes()
                ));
            }
            yearDto.setMonths(months);
            years.add(yearDto);
        }

        res.setYears(years);
        return res;
    }
}
