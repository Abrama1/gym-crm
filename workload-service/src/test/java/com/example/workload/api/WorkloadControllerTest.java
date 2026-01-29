package com.example.workload.api;

import com.example.workload.dto.ActionType;
import com.example.workload.dto.TrainerWorkloadResponse;
import com.example.workload.dto.WorkloadEventRequest;
import com.example.workload.service.WorkloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkloadController.class)
class WorkloadControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockBean private WorkloadService service;

    @Test
    void postEvents_ok() throws Exception {
        WorkloadEventRequest req = new WorkloadEventRequest();
        req.setTrainerUsername("t1");
        req.setTrainerFirstName("John");
        req.setTrainerLastName("Doe");
        req.setActive(true);
        req.setTrainingDate(LocalDate.of(2026, 1, 10));
        req.setTrainingDurationMinutes(60);
        req.setActionType(ActionType.ADD);

        mvc.perform(post("/api/workloads/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(service).applyEvent(any(WorkloadEventRequest.class));
    }

    @Test
    void getMonth_ok() throws Exception {
        when(service.getMonthMinutes("t1", 2026, 1)).thenReturn(90);

        mvc.perform(get("/api/workloads/t1/months/2026/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("t1"))
                .andExpect(jsonPath("$.year").value(2026))
                .andExpect(jsonPath("$.month").value(1))
                .andExpect(jsonPath("$.totalMinutes").value(90));

        verify(service).getMonthMinutes("t1", 2026, 1);
    }

    @Test
    void getSummary_ok() throws Exception {
        TrainerWorkloadResponse res = new TrainerWorkloadResponse();
        res.setTrainerUsername("t1");
        res.setTrainerFirstName("John");
        res.setTrainerLastName("Doe");
        res.setActive(true);

        TrainerWorkloadResponse.YearSummary y = new TrainerWorkloadResponse.YearSummary();
        y.setYear(2026);

        y.setMonths(List.of(new TrainerWorkloadResponse.MonthSummary(1, 60)));
        res.setYears(List.of(y));

        when(service.getTrainerWorkload("t1")).thenReturn(res);

        mvc.perform(get("/api/workloads/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("t1"))
                .andExpect(jsonPath("$.years[0].year").value(2026))
                .andExpect(jsonPath("$.years[0].months[0].month").value(1))
                .andExpect(jsonPath("$.years[0].months[0].trainingSummaryDurationMinutes").value(60));
    }
}
