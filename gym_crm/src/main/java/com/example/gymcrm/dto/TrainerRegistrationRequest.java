package com.example.gymcrm.dto;

import jakarta.validation.constraints.NotBlank;

public class TrainerRegistrationRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String specialization; // training type name

    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public String getSpecialization() {return specialization;}
    public void setSpecialization(String specialization) {this.specialization = specialization;}
}
