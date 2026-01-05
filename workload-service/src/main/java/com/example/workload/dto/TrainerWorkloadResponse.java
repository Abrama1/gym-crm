package com.example.workload.dto;

import java.util.ArrayList;
import java.util.List;

public class TrainerWorkloadResponse {

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean active;

    private List<YearSummary> years = new ArrayList<>();

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getTrainerFirstName() { return trainerFirstName; }
    public void setTrainerFirstName(String trainerFirstName) { this.trainerFirstName = trainerFirstName; }

    public String getTrainerLastName() { return trainerLastName; }
    public void setTrainerLastName(String trainerLastName) { this.trainerLastName = trainerLastName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<YearSummary> getYears() { return years; }
    public void setYears(List<YearSummary> years) { this.years = years; }

    public static class YearSummary {
        private int year;
        private List<MonthSummary> months = new ArrayList<>();

        public YearSummary() {}
        public YearSummary(int year) { this.year = year; }

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }

        public List<MonthSummary> getMonths() { return months; }
        public void setMonths(List<MonthSummary> months) { this.months = months; }
    }

    public static class MonthSummary {
        private int month; // 1..12
        private int trainingSummaryDurationMinutes;

        public MonthSummary() {}
        public MonthSummary(int month, int minutes) {
            this.month = month;
            this.trainingSummaryDurationMinutes = minutes;
        }

        public int getMonth() { return month; }
        public void setMonth(int month) { this.month = month; }

        public int getTrainingSummaryDurationMinutes() { return trainingSummaryDurationMinutes; }
        public void setTrainingSummaryDurationMinutes(int trainingSummaryDurationMinutes) {
            this.trainingSummaryDurationMinutes = trainingSummaryDurationMinutes;
        }
    }
}
