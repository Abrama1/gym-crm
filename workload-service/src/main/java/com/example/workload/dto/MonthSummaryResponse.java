package com.example.workload.dto;

public class MonthSummaryResponse {
    private String trainerUsername;
    private int year;
    private int month;
    private int totalMinutes;

    public MonthSummaryResponse() {}

    public MonthSummaryResponse(String trainerUsername, int year, int month, int totalMinutes) {
        this.trainerUsername = trainerUsername;
        this.year = year;
        this.month = month;
        this.totalMinutes = totalMinutes;
    }

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }
}
