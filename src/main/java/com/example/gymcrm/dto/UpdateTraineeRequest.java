package com.example.gymcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class UpdateTraineeRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    @NotNull private Boolean active;

    public String getFirstName(){return firstName;}
    public void setFirstName(String firstName){this.firstName=firstName;}
    public String getLastName(){return lastName;}
    public void setLastName(String lastName){this.lastName=lastName;}
    public LocalDate getDateOfBirth(){return dateOfBirth;}
    public void setDateOfBirth(LocalDate dateOfBirth){this.dateOfBirth=dateOfBirth;}
    public String getAddress(){return address;}
    public void setAddress(String address){this.address=address;}
    public Boolean getActive(){return active;}
    public void setActive(Boolean active){this.active=active;}
}
