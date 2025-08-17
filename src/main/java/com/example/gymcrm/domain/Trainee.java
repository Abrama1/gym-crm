package com.example.gymcrm.domain;

import java.time.LocalDate;

public class Trainee {
    private Long id;
    private LocalDate dateOfBirth;
    private String address;
    private Long userId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
