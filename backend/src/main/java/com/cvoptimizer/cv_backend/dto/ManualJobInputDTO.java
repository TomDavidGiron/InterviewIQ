package com.cvoptimizer.cv_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ManualJobInputDTO {

    @Size(max = 255, message = "title must be at most 255 characters")
    private String title;

    @Size(max = 255, message = "company must be at most 255 characters")
    private String company;

    @Size(max = 255, message = "location must be at most 255 characters")
    private String location;

    @NotBlank(message = "description is required")
    @Size(max = 50_000, message = "description must be at most 50000 characters")
    private String description;

    private List<String> requirements;

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRequirements() { return requirements; }
    public void setRequirements(List<String> requirements) { this.requirements = requirements; }
}
