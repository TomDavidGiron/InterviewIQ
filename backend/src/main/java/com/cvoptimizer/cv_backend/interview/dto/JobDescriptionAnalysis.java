package com.cvoptimizer.cv_backend.interview.dto;

import java.util.ArrayList;
import java.util.List;

public class JobDescriptionAnalysis {

    private String normalizedJobText;
    private String roleHint;
    private String seniorityHint;
    private String domainHint;

    private List<String> extractedSkills = new ArrayList<>();
    private List<String> mustHaveSkills = new ArrayList<>();
    private List<String> niceToHaveSkills = new ArrayList<>();
    private List<String> planningNotes = new ArrayList<>();

    public String getNormalizedJobText() {
        return normalizedJobText;
    }

    public void setNormalizedJobText(String normalizedJobText) {
        this.normalizedJobText = normalizedJobText;
    }

    public String getRoleHint() {
        return roleHint;
    }

    public void setRoleHint(String roleHint) {
        this.roleHint = roleHint;
    }

    public String getSeniorityHint() {
        return seniorityHint;
    }

    public void setSeniorityHint(String seniorityHint) {
        this.seniorityHint = seniorityHint;
    }

    public String getDomainHint() {
        return domainHint;
    }

    public void setDomainHint(String domainHint) {
        this.domainHint = domainHint;
    }

    public List<String> getExtractedSkills() {
        return extractedSkills;
    }

    public void setExtractedSkills(List<String> extractedSkills) {
        this.extractedSkills = extractedSkills;
    }

    public List<String> getMustHaveSkills() {
        return mustHaveSkills;
    }

    public void setMustHaveSkills(List<String> mustHaveSkills) {
        this.mustHaveSkills = mustHaveSkills;
    }

    public List<String> getNiceToHaveSkills() {
        return niceToHaveSkills;
    }

    public void setNiceToHaveSkills(List<String> niceToHaveSkills) {
        this.niceToHaveSkills = niceToHaveSkills;
    }

    public List<String> getPlanningNotes() {
        return planningNotes;
    }

    public void setPlanningNotes(List<String> planningNotes) {
        this.planningNotes = planningNotes;
    }
}