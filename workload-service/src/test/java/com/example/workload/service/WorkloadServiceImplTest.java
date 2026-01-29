package com.example.workload.service;

import com.example.workload.dto.ActionType;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.mongo.TrainerWorkloadDocument;
import com.example.workload.mongo.TrainerWorkloadRepository;
import com.example.workload.service.impl.WorkloadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkloadServiceImplTest {

    private TrainerWorkloadRepository repo;
    private WorkloadServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = mock(TrainerWorkloadRepository.class);
        service = new WorkloadServiceImpl(repo);
    }

    @Test
    void applyEvent_add_createsDocAndAddsMinutes() {
        WorkloadEventRequest req = baseReq(ActionType.ADD, LocalDate.of(2026, 1, 10), 60);

        when(repo.findById("trainer1")).thenReturn(Optional.empty());

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);

        MDC.put("txId", "tx-1");
        service.applyEvent(req);
        MDC.remove("txId");

        verify(repo).save(captor.capture());
        TrainerWorkloadDocument saved = captor.getValue();

        assertEquals("trainer1", saved.getTrainerUsername());
        assertEquals("John", saved.getTrainerFirstName());
        assertEquals("Doe", saved.getTrainerLastName());
        assertTrue(saved.isActive());

        assertEquals(1, saved.getYears().size());
        var y = saved.getYears().get(0);
        assertEquals(2026, y.getYear());
        assertEquals(1, y.getMonths().size());
        var m = y.getMonths().get(0);
        assertEquals(1, m.getMonth());
        assertEquals(60, m.getTrainingSummaryDurationMinutes());
    }

    @Test
    void applyEvent_delete_decreasesMinutesButNotBelowZero() {
        // existing doc has 30 mins in Jan 2026
        TrainerWorkloadDocument doc = new TrainerWorkloadDocument();
        doc.setTrainerUsername("trainer1");
        doc.setTrainerFirstName("John");
        doc.setTrainerLastName("Doe");
        doc.setActive(true);

        var y = new TrainerWorkloadDocument.YearEntry(2026);
        y.getMonths().add(new TrainerWorkloadDocument.MonthEntry(1, 30));
        doc.getYears().add(y);

        when(repo.findById("trainer1")).thenReturn(Optional.of(doc));

        WorkloadEventRequest req = baseReq(ActionType.DELETE, LocalDate.of(2026, 1, 10), 60);

        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        service.applyEvent(req);

        verify(repo).save(captor.capture());
        TrainerWorkloadDocument saved = captor.getValue();

        int minutes = saved.getYears().get(0).getMonths().get(0).getTrainingSummaryDurationMinutes();
        assertEquals(0, minutes); // clamp to 0
    }

    @Test
    void getMonthMinutes_returnsZeroIfNoDoc() {
        when(repo.findById("x")).thenReturn(Optional.empty());
        assertEquals(0, service.getMonthMinutes("x", 2026, 1));
    }

    @Test
    void getMonthMinutes_returnsValueIfExists() {
        TrainerWorkloadDocument doc = new TrainerWorkloadDocument();
        doc.setTrainerUsername("trainer1");
        var y = new TrainerWorkloadDocument.YearEntry(2026);
        y.getMonths().add(new TrainerWorkloadDocument.MonthEntry(2, 120));
        doc.getYears().add(y);

        when(repo.findById("trainer1")).thenReturn(Optional.of(doc));

        assertEquals(120, service.getMonthMinutes("trainer1", 2026, 2));
        assertEquals(0, service.getMonthMinutes("trainer1", 2026, 1));
    }

    @Test
    void applyEvent_validation_missingUsername_throws() {
        WorkloadEventRequest req = baseReq(ActionType.ADD, LocalDate.of(2026, 1, 1), 30);
        req.setTrainerUsername("  ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.applyEvent(req));
        assertTrue(ex.getMessage().toLowerCase().contains("trainerusername"));
        verify(repo, never()).save(any());
    }

    private static WorkloadEventRequest baseReq(ActionType type, LocalDate date, int mins) {
        WorkloadEventRequest r = new WorkloadEventRequest();
        r.setTrainerUsername("trainer1");
        r.setTrainerFirstName("John");
        r.setTrainerLastName("Doe");
        r.setActive(true);
        r.setTrainingDate(date);
        r.setTrainingDurationMinutes(mins);
        r.setActionType(type);
        return r;
    }
}
