package com.example.gymcrm.dto;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UpdateTraineeTrainersRequest {
    @NotNull
    private List<String> trainers = new ArrayList<>(); // trainer usernames

    public List<String> getTrainers() { return trainers; }
    public void setTrainers(List<String> trainers) { this.trainers = trainers; }
}
