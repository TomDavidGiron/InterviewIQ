package com.cvoptimizer.cv_backend.interview.dto;

import com.cvoptimizer.cv_backend.interview.model.InterviewStatus;

import java.util.List;

public class InterviewSummaryResponse {

    // existing
    private InterviewStatus status;

    private int totalQuestions;
    private int answeredCount;

    private int scoreSoFar;
    private int maxScoreSoFar;

    private int scorePercent; // 0-100

    private QuestionDto lastQuestion;

    private List<TagWeaknessDto> weakTags;
    private List<String> recommendations;


    private int overallScore;          // 0-100 (alias of scorePercent for now)
    private boolean passed;
    private String passReason;

    private List<TagBreakdownDto> tagBreakdown;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> criticalFailures;

    private DiagnosisDto diagnosis;

    public InterviewSummaryResponse() {}

    public InterviewSummaryResponse(InterviewStatus status,
                                    int totalQuestions,
                                    int answeredCount,
                                    int scoreSoFar,
                                    int maxScoreSoFar,
                                    int scorePercent,
                                    QuestionDto lastQuestion,
                                    List<TagWeaknessDto> weakTags,
                                    List<String> recommendations) {
        this.status = status;
        this.totalQuestions = totalQuestions;
        this.answeredCount = answeredCount;
        this.scoreSoFar = scoreSoFar;
        this.maxScoreSoFar = maxScoreSoFar;
        this.scorePercent = scorePercent;
        this.lastQuestion = lastQuestion;
        this.weakTags = weakTags;
        this.recommendations = recommendations;

        // defaults for new fields
        this.overallScore = scorePercent;
        this.passed = (status != null && status.name().equals("PASSED"));
        this.passReason = null;
        this.tagBreakdown = List.of();
        this.strengths = List.of();
        this.weaknesses = List.of();
        this.criticalFailures = List.of();
        this.diagnosis = null;
    }

    public InterviewSummaryResponse(InterviewStatus status,
                                    int totalQuestions,
                                    int answeredCount,
                                    int scoreSoFar,
                                    int maxScoreSoFar,
                                    int scorePercent,
                                    QuestionDto lastQuestion,
                                    List<TagWeaknessDto> weakTags,
                                    List<String> recommendations,
                                    int overallScore,
                                    boolean passed,
                                    String passReason,
                                    List<TagBreakdownDto> tagBreakdown,
                                    List<String> strengths,
                                    List<String> weaknesses,
                                    List<String> criticalFailures,
                                    DiagnosisDto diagnosis) {
        this.status = status;
        this.totalQuestions = totalQuestions;
        this.answeredCount = answeredCount;
        this.scoreSoFar = scoreSoFar;
        this.maxScoreSoFar = maxScoreSoFar;
        this.scorePercent = scorePercent;
        this.lastQuestion = lastQuestion;
        this.weakTags = weakTags;
        this.recommendations = recommendations;

        this.overallScore = overallScore;
        this.passed = passed;
        this.passReason = passReason;
        this.tagBreakdown = tagBreakdown;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.criticalFailures = criticalFailures;
        this.diagnosis = diagnosis;
    }

    public InterviewStatus getStatus() { return status; }
    public void setStatus(InterviewStatus status) { this.status = status; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getAnsweredCount() { return answeredCount; }
    public void setAnsweredCount(int answeredCount) { this.answeredCount = answeredCount; }

    public int getScoreSoFar() { return scoreSoFar; }
    public void setScoreSoFar(int scoreSoFar) { this.scoreSoFar = scoreSoFar; }

    public int getMaxScoreSoFar() { return maxScoreSoFar; }
    public void setMaxScoreSoFar(int maxScoreSoFar) { this.maxScoreSoFar = maxScoreSoFar; }

    public int getScorePercent() { return scorePercent; }
    public void setScorePercent(int scorePercent) { this.scorePercent = scorePercent; }

    public QuestionDto getLastQuestion() { return lastQuestion; }
    public void setLastQuestion(QuestionDto lastQuestion) { this.lastQuestion = lastQuestion; }

    public List<TagWeaknessDto> getWeakTags() { return weakTags; }
    public void setWeakTags(List<TagWeaknessDto> weakTags) { this.weakTags = weakTags; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public String getPassReason() { return passReason; }
    public void setPassReason(String passReason) { this.passReason = passReason; }

    public List<TagBreakdownDto> getTagBreakdown() { return tagBreakdown; }
    public void setTagBreakdown(List<TagBreakdownDto> tagBreakdown) { this.tagBreakdown = tagBreakdown; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public List<String> getCriticalFailures() { return criticalFailures; }
    public void setCriticalFailures(List<String> criticalFailures) { this.criticalFailures = criticalFailures; }

    public DiagnosisDto getDiagnosis() { return diagnosis; }
    public void setDiagnosis(DiagnosisDto diagnosis) { this.diagnosis = diagnosis; }
}