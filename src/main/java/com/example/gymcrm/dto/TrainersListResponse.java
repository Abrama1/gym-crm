package com.example.gymcrm.dto;

import java.util.ArrayList;
import java.util.List;

public class TrainersListResponse {
    private List<TrainerSummary> items = new ArrayList<>();

    public List<TrainerSummary> getItems() { return items; }
    public void setItems(List<TrainerSummary> items) { this.items = items; }
}
