package com.example.gymcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateTrainerRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    // specialization is read-only
    @NotNull private Boolean active;

    public String getFirstName(){return firstName;}
    public void setFirstName(String firstName){this.firstName=firstName;}
    public String getLastName(){return lastName;}
    public void setLastName(String lastName){this.lastName=lastName;}
    public Boolean getActive(){return active;}
    public void setActive(Boolean active){this.active=active;}
}
