package com.cvoptimizer.cv_backend.interview.dto;

import java.util.List;

public class DiagnosisDto {

    private String overallAssessment;
    private int overallScore;

    private List<String> primaryWeakAreas;
    private List<String> criticalIssues;
    private List<String> suggestedStudyPlan;

    public DiagnosisDto() {
    }

    public DiagnosisDto(String overallAssessment,
                        int overallScore,
                        List<String> primaryWeakAreas,
                        List<String> criticalIssues,
                        List<String> suggestedStudyPlan) {
        this.overallAssessment = overallAssessment;
        this.overallScore = overallScore;
        this.primaryWeakAreas = primaryWeakAreas;
        this.criticalIssues = criticalIssues;
        this.suggestedStudyPlan = suggestedStudyPlan;
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
        this.primaryWeakAreas = primaryWeakAreas;
    }

    public List<String> getCriticalIssues() {
        return criticalIssues;
    }

    public void setCriticalIssues(List<String> criticalIssues) {
        this.criticalIssues = criticalIssues;
    }

    public List<String> getSuggestedStudyPlan() {
        return suggestedStudyPlan;
    }

    public void setSuggestedStudyPlan(List<String> suggestedStudyPlan) {
        this.suggestedStudyPlan = suggestedStudyPlan;
    }
}