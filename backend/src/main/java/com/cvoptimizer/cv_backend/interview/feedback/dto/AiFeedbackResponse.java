package com.cvoptimizer.cv_backend.interview.feedback.dto;

import java.util.List;

public class AiFeedbackResponse {

    private String diagnosis;
    private List<String> strengths;
    private List<String> weaknesses;
    private String studyPlan;
    private String feedbackSummary;

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public String getStudyPlan() { return studyPlan; }
    public void setStudyPlan(String studyPlan) { this.studyPlan = studyPlan; }

    public String getFeedbackSummary() { return feedbackSummary; }
    public void setFeedbackSummary(String feedbackSummary) { this.feedbackSummary = feedbackSummary; }
}