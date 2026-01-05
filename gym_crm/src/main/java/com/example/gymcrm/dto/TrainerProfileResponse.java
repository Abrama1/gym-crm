package com.example.gymcrm.dto;

import java.util.List;

public class TrainerProfileResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
    private boolean active;
    private List<TraineeSummary> trainees;

    public static class TraineeSummary {
        public String username;
        public String firstName;
        public String lastName;
        public TraineeSummary() {}
        public TraineeSummary(String u,String f,String l){username=u;firstName=f;lastName=l;}
    }

    // getters/setters
    public String getUsername(){return username;}
    public void setUsername(String username){this.username=username;}
    public String getFirstName(){return firstName;}
    public void setFirstName(String firstName){this.firstName=firstName;}
    public String getLastName(){return lastName;}
    public void setLastName(String lastName){this.lastName=lastName;}
    public String getSpecialization(){return specialization;}
    public void setSpecialization(String specialization){this.specialization=specialization;}
    public boolean isActive(){return active;}
    public void setActive(boolean active){this.active=active;}
    public List<TraineeSummary> getTrainees(){return trainees;}
    public void setTrainees(List<TraineeSummary> trainees){this.trainees=trainees;}
}
