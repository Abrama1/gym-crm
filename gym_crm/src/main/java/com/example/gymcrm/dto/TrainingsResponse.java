package com.example.gymcrm.dto;

import java.util.ArrayList;
import java.util.List;

public class TrainingsResponse {
    private List<TrainingItemResponse> items = new ArrayList<>();

    public List<TrainingItemResponse> getItems() { return items; }
    public void setItems(List<TrainingItemResponse> items) { this.items = items; }
}
