package com.example.workload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class WorkloadEventRequest {

    @NotBlank
    private String trainerUsername;

    @NotBlank
    private String trainerFirstName;

    @NotBlank
    private String trainerLastName;

    @NotNull
    private Boolean active;

    @NotNull
    private LocalDate trainingDate;

    @NotNull
    @Positive
    private Integer trainingDurationMinutes;

    @NotNull
    private ActionType actionType;

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getTrainerFirstName() { return trainerFirstName; }
    public void setTrainerFirstName(String trainerFirstName) { this.trainerFirstName = trainerFirstName; }

    public String getTrainerLastName() { return trainerLastName; }
    public void setTrainerLastName(String trainerLastName) { this.trainerLastName = trainerLastName; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }

    public Integer getTrainingDurationMinutes() { return trainingDurationMinutes; }
    public void setTrainingDurationMinutes(Integer trainingDurationMinutes) { this.trainingDurationMinutes = trainingDurationMinutes; }

    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }
}
