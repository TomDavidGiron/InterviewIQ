package com.cvoptimizer.cv_backend.interview.dto;

import java.util.ArrayList;
import java.util.List;

public class InterviewSummaryResponse {

    private String sessionId;
    private int totalScore;
    private int maxScore;
    private double percentage;
    private String overallScore;
    private List<String> strengths = new ArrayList<>();
    private List<String> weaknesses = new ArrayList<>();
    private List<String> weakTopics = new ArrayList<>();
    private List<TagBreakdownDto> tagBreakdown = new ArrayList<>();
    private List<TagWeaknessDto> tagWeaknesses = new ArrayList<>();
    private DiagnosisDto diagnosis;

    // AI Feedback Engine
    private String studyPlan;
    private String feedbackSummary;
    private String feedbackSource;

    // Phase 6
    private List<String> matchedJobSkills = new ArrayList<>();
    private List<String> missingJobSkills = new ArrayList<>();
    private Integer jobFitScore;
    private String targetRoleHint;
    private String targetSeniorityHint;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(String overallScore) {
        this.overallScore = overallScore;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public List<String> getWeakTopics() {
        return weakTopics;
    }

    public void setWeakTopics(List<String> weakTopics) {
        this.weakTopics = weakTopics;
    }

    public List<TagBreakdownDto> getTagBreakdown() {
        return tagBreakdown;
    }

    public void setTagBreakdown(List<TagBreakdownDto> tagBreakdown) {
        this.tagBreakdown = tagBreakdown;
    }

    public List<TagWeaknessDto> getTagWeaknesses() {
        return tagWeaknesses;
    }

    public void setTagWeaknesses(List<TagWeaknessDto> tagWeaknesses) {
        this.tagWeaknesses = tagWeaknesses;
    }

    public DiagnosisDto getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(DiagnosisDto diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getStudyPlan() {
        return studyPlan;
    }

    public void setStudyPlan(String studyPlan) {
        this.studyPlan = studyPlan;
    }

    public String getFeedbackSummary() {
        return feedbackSummary;
    }

    public void setFeedbackSummary(String feedbackSummary) {
        this.feedbackSummary = feedbackSummary;
    }

    public String getFeedbackSource() {
        return feedbackSource;
    }

    public void setFeedbackSource(String feedbackSource) {
        this.feedbackSource = feedbackSource;
    }

    public List<String> getMatchedJobSkills() {
        return matchedJobSkills;
    }

    public void setMatchedJobSkills(List<String> matchedJobSkills) {
        this.matchedJobSkills = matchedJobSkills;
    }

    public List<String> getMissingJobSkills() {
        return missingJobSkills;
    }

    public void setMissingJobSkills(List<String> missingJobSkills) {
        this.missingJobSkills = missingJobSkills;
    }

    public Integer getJobFitScore() {
        return jobFitScore;
    }

    public void setJobFitScore(Integer jobFitScore) {
        this.jobFitScore = jobFitScore;
    }

    public String getTargetRoleHint() {
        return targetRoleHint;
    }

    public void setTargetRoleHint(String targetRoleHint) {
        this.targetRoleHint = targetRoleHint;
    }

    public String getTargetSeniorityHint() {
        return targetSeniorityHint;
    }

    public void setTargetSeniorityHint(String targetSeniorityHint) {
        this.targetSeniorityHint = targetSeniorityHint;
    }
}