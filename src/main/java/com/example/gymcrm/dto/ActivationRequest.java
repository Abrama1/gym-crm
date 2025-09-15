package com.example.gymcrm.dto;

import jakarta.validation.constraints.NotNull;

public class ActivationRequest {
    @NotNull private Boolean active;
    public Boolean getActive(){return active;}
    public void setActive(Boolean active){this.active=active;}
}
