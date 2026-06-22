package com.cvoptimizer.cv_backend.interview.dto;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisDto {

    private String overallAssessment;
    private int overallScore;
    private List<String> primaryWeakAreas = new ArrayList<>();
    private List<String> criticalIssues = new ArrayList<>();
    private List<String> suggestedStudyPlan = new ArrayList<>();

    // Phase 6 additions
    private String targetRole;
    private Integer jobFitScore;
    private List<String> jobCriticalWeakAreas = new ArrayList<>();

    public DiagnosisDto() {
    }

    public String getOverallAssessment() {
        return overallAssessment;
    }

    public void setOverallAssessment(String overallAssessment) {
        this.overallAssessment = overallAssessment;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public List<String> getPrimaryWeakAreas() {
        return primaryWeakAreas;
    }

    public void setPrimaryWeakAreas(List<String> primaryWeakAreas) {
        this.primaryWeakAreas = primaryWeakAreas != null ? primaryWeakAreas : new ArrayList<>();
    }

    public List<String> getCriticalIssues() {
        return criticalIssues;
    }

    public void setCriticalIssues(List<String> criticalIssues) {
        this.criticalIssues = criticalIssues != null ? criticalIssues : new ArrayList<>();
    }

    public List<String> getSuggestedStudyPlan() {
        return suggestedStudyPlan;
    }

    public void setSuggestedStudyPlan(List<String> suggestedStudyPlan) {
        this.suggestedStudyPlan = suggestedStudyPlan != null ? suggestedStudyPlan : new ArrayList<>();
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public Integer getJobFitScore() {
        return jobFitScore;
    }

    public void setJobFitScore(Integer jobFitScore) {
        this.jobFitScore = jobFitScore;
    }

    public List<String> getJobCriticalWeakAreas() {
        return jobCriticalWeakAreas;
    }

    public void setJobCriticalWeakAreas(List<String> jobCriticalWeakAreas) {
        this.jobCriticalWeakAreas = jobCriticalWeakAreas != null ? jobCriticalWeakAreas : new ArrayList<>();
    }
}