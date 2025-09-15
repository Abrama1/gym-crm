package com.example.gymcrm.dto;

import java.time.LocalDate;

public class TrainingItemResponse {
    private String trainingName;
    private LocalDate trainingDate;
    private String trainingType;
    private int trainingDuration;
    private String otherPartyName; // trainer full name for trainee view; trainee full name for trainer view

    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }

    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }

    public int getTrainingDuration() { return trainingDuration; }
    public void setTrainingDuration(int trainingDuration) { this.trainingDuration = trainingDuration; }

    public String getOtherPartyName() { return otherPartyName; }
    public void setOtherPartyName(String otherPartyName) { this.otherPartyName = otherPartyName; }
}
