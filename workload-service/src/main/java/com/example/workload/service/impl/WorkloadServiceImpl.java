package com.example.workload.service.impl;

import com.example.workload.dto.ActionType;
import com.example.workload.dto.TrainerWorkloadResponse;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.mongo.TrainerWorkloadDocument;
import com.example.workload.mongo.TrainerWorkloadRepository;
import com.example.workload.service.WorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Comparator;

@Service
public class WorkloadServiceImpl implements WorkloadService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadServiceImpl.class);

    private final TrainerWorkloadRepository repo;

    public WorkloadServiceImpl(TrainerWorkloadRepository repo) {
        this.repo = repo;
    }

    @Override
    public void applyEvent(WorkloadEventRequest req) {
        validate(req);

        String txId = MDC.get("txId");
        LocalDate d = req.getTrainingDate();

        int year = d.getYear();
        int month = d.getMonthValue();

        int delta = req.getTrainingDurationMinutes();
        if (req.getActionType() == ActionType.DELETE) {
            delta = -delta;
        }

        // transaction-level log (start)
        log.info("tx={} workload event received trainer={} action={} date={} duration={}",
                txId,
                req.getTrainerUsername(),
                req.getActionType(),
                req.getTrainingDate(),
                req.getTrainingDurationMinutes()
        );

        TrainerWorkloadDocument doc = repo.findById(req.getTrainerUsername())
                .orElseGet(() -> {
                    TrainerWorkloadDocument n = new TrainerWorkloadDocument();
                    n.setTrainerUsername(req.getTrainerUsername());
                    n.setTrainerFirstName(req.getTrainerFirstName());
                    n.setTrainerLastName(req.getTrainerLastName());
                    n.setActive(Boolean.TRUE.equals(req.getActive()));
                    return n;
                });

        // keep trainer profile info up to date
        doc.setTrainerFirstName(req.getTrainerFirstName());
        doc.setTrainerLastName(req.getTrainerLastName());
        doc.setActive(Boolean.TRUE.equals(req.getActive()));

        TrainerWorkloadDocument.YearEntry y = doc.getYears().stream()
                .filter(e -> e.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    TrainerWorkloadDocument.YearEntry ny = new TrainerWorkloadDocument.YearEntry(year);
                    doc.getYears().add(ny);
                    return ny;
                });

        TrainerWorkloadDocument.MonthEntry m = y.getMonths().stream()
                .filter(e -> e.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    TrainerWorkloadDocument.MonthEntry nm = new TrainerWorkloadDocument.MonthEntry(month, 0);
                    y.getMonths().add(nm);
                    return nm;
                });

        int before = m.getTrainingSummaryDurationMinutes();
        int after = before + delta;
        if (after < 0) after = 0; // never negative

        m.setTrainingSummaryDurationMinutes(after);

        // stable ordering for deterministic output
        doc.getYears().sort(Comparator.comparingInt(TrainerWorkloadDocument.YearEntry::getYear));
        for (TrainerWorkloadDocument.YearEntry ye : doc.getYears()) {
            ye.getMonths().sort(Comparator.comparingInt(TrainerWorkloadDocument.MonthEntry::getMonth));
        }

        repo.save(doc);

        // operation-level log (end)
        log.info("tx={} workload updated trainer={} {}-{} before={} after={}",
                txId,
                req.getTrainerUsername(),
                year, month,
                before, after
        );
    }

    @Override
    public int getMonthMinutes(String trainerUsername, int year, int month) {
        TrainerWorkloadDocument doc = repo.findById(trainerUsername).orElse(null);
        if (doc == null) return 0;

        return doc.getYears().stream()
                .filter(y -> y.getYear() == year)
                .flatMap(y -> y.getMonths().stream())
                .filter(m -> m.getMonth() == month)
                .map(TrainerWorkloadDocument.MonthEntry::getTrainingSummaryDurationMinutes)
                .findFirst()
                .orElse(0);
    }

    @Override
    public TrainerWorkloadResponse getTrainerWorkload(String trainerUsername) {
        TrainerWorkloadDocument doc = repo.findById(trainerUsername).orElse(null);
        if (doc == null) {
            TrainerWorkloadResponse empty = new TrainerWorkloadResponse();
            empty.setTrainerUsername(trainerUsername);
            empty.setYears(java.util.List.of());
            return empty;
        }

        TrainerWorkloadResponse res = new TrainerWorkloadResponse();
        res.setTrainerUsername(doc.getTrainerUsername());
        res.setTrainerFirstName(doc.getTrainerFirstName());
        res.setTrainerLastName(doc.getTrainerLastName());
        res.setActive(doc.isActive());

        var years = new java.util.ArrayList<TrainerWorkloadResponse.YearSummary>();
        for (TrainerWorkloadDocument.YearEntry y : doc.getYears()) {
            var yr = new TrainerWorkloadResponse.YearSummary();
            yr.setYear(y.getYear());

            var months = new java.util.ArrayList<TrainerWorkloadResponse.MonthSummary>();
            for (TrainerWorkloadDocument.MonthEntry m : y.getMonths()) {
                months.add(new TrainerWorkloadResponse.MonthSummary(
                        m.getMonth(),
                        m.getTrainingSummaryDurationMinutes()
                ));
            }
            yr.setMonths(months);
            years.add(yr);
        }
        res.setYears(years);
        return res;
    }

    private void validate(WorkloadEventRequest req) {
        if (!StringUtils.hasText(req.getTrainerUsername())) {
            throw new IllegalArgumentException("trainerUsername is required");
        }
        if (!StringUtils.hasText(req.getTrainerFirstName())) {
            throw new IllegalArgumentException("trainerFirstName is required");
        }
        if (!StringUtils.hasText(req.getTrainerLastName())) {
            throw new IllegalArgumentException("trainerLastName is required");
        }
        if (req.getActive() == null) {
            throw new IllegalArgumentException("active is required");
        }
        if (req.getTrainingDate() == null) {
            throw new IllegalArgumentException("trainingDate is required");
        }
        if (req.getTrainingDurationMinutes() == null || req.getTrainingDurationMinutes() <= 0) {
            throw new IllegalArgumentException("trainingDurationMinutes must be positive");
        }
        if (req.getActionType() == null) {
            throw new IllegalArgumentException("actionType is required");
        }
    }
}
