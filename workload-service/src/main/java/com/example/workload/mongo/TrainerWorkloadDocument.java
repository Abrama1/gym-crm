package com.example.workload.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "trainer_workloads")
@CompoundIndex(
        name = "idx_trainer_first_last",
        def = "{'trainerFirstName': 1, 'trainerLastName': 1}"
)
public class TrainerWorkloadDocument {

    @Id
    private String trainerUsername;

    private String trainerFirstName;
    private String trainerLastName;

    // Trainer Status
    private boolean active;

    // Years List
    private List<YearEntry> years = new ArrayList<>();

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getTrainerFirstName() { return trainerFirstName; }
    public void setTrainerFirstName(String trainerFirstName) { this.trainerFirstName = trainerFirstName; }

    public String getTrainerLastName() { return trainerLastName; }
    public void setTrainerLastName(String trainerLastName) { this.trainerLastName = trainerLastName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<YearEntry> getYears() { return years; }
    public void setYears(List<YearEntry> years) { this.years = years; }

    // ---- nested entries ----

    public static class YearEntry {
        private int year;
        private List<MonthEntry> months = new ArrayList<>();

        public YearEntry() {}
        public YearEntry(int year) { this.year = year; }

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }

        public List<MonthEntry> getMonths() { return months; }
        public void setMonths(List<MonthEntry> months) { this.months = months; }
    }

    public static class MonthEntry {
        private int month; // 1..12
        private int trainingSummaryDurationMinutes; // number type requirement

        public MonthEntry() {}
        public MonthEntry(int month, int minutes) {
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
