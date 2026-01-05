package com.example.workload.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workload_summary")
public class WorkloadSummary {

    @EmbeddedId
    private WorkloadSummaryId id;

    @Column(name = "trainer_first_name", nullable = false)
    private String trainerFirstName;

    @Column(name = "trainer_last_name", nullable = false)
    private String trainerLastName;

    @Column(name = "trainer_active", nullable = false)
    private boolean active;

    @Column(name = "total_minutes", nullable = false)
    private int totalMinutes;

    public WorkloadSummaryId getId() { return id; }
    public void setId(WorkloadSummaryId id) { this.id = id; }

    public String getTrainerFirstName() { return trainerFirstName; }
    public void setTrainerFirstName(String trainerFirstName) { this.trainerFirstName = trainerFirstName; }

    public String getTrainerLastName() { return trainerLastName; }
    public void setTrainerLastName(String trainerLastName) { this.trainerLastName = trainerLastName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }
}
