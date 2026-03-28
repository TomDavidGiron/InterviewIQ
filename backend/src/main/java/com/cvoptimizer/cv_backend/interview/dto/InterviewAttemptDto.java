package com.cvoptimizer.cv_backend.interview.dto;

import java.time.Instant;
import java.util.List;

public class InterviewAttemptDto {

    private String attemptId;
    private String questionId;
    private String questionText;
    private String answerText;
    private String feedback;
    private boolean passed;
    private int earnedPoints;
    private int maxPoints;
    private List<String> missingKeywords;
    private Instant createdAt;

    public InterviewAttemptDto() {}

    public InterviewAttemptDto(
            String attemptId,
            String questionId,
            String questionText,
            String answerText,
            String feedback,
            boolean passed,
            int earnedPoints,
            int maxPoints,
            List<String> missingKeywords,
            Instant createdAt
    ) {
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.questionText = questionText;
        this.answerText = answerText;
        this.feedback = feedback;
        this.passed = passed;
        this.earnedPoints = earnedPoints;
        this.maxPoints = maxPoints;
        this.missingKeywords = missingKeywords;
        this.createdAt = createdAt;
    }

    public String getAttemptId() { return attemptId; }
    public void setAttemptId(String attemptId) { this.attemptId = attemptId; }

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public int getEarnedPoints() { return earnedPoints; }
    public void setEarnedPoints(int earnedPoints) { this.earnedPoints = earnedPoints; }

    public int getMaxPoints() { return maxPoints; }
    public void setMaxPoints(int maxPoints) { this.maxPoints = maxPoints; }

    public List<String> getMissingKeywords() { return missingKeywords; }
    public void setMissingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
