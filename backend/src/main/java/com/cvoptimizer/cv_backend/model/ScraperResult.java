package com.cvoptimizer.cv_backend.model;

import java.util.List;

public class ScraperResult {
    private String title;
    private String company;
    private String location;
    private String description;
    private List<String> requirements;

    public ScraperResult() {}

    public ScraperResult(String title, String company, String location, String description, List<String> requirements) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
        this.requirements = requirements;
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }
}
