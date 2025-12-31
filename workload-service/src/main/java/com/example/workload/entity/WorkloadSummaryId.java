package com.example.workload.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WorkloadSummaryId implements Serializable {

    private String trainerUsername;
    private int year;
    private int month;

    public WorkloadSummaryId() {}

    public WorkloadSummaryId(String trainerUsername, int year, int month) {
        this.trainerUsername = trainerUsername;
        this.year = year;
        this.month = month;
    }

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkloadSummaryId that)) return false;
        return year == that.year && month == that.month && Objects.equals(trainerUsername, that.trainerUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainerUsername, year, month);
    }
}
