package com.cvoptimizer.cv_backend.interview.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "interview_attempt")
public class InterviewAttemptEntity {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSessionEntity session;

    private String questionId;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String answerText;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private boolean passed;

    private int earnedPoints;
    private int maxPoints;

    @Column(columnDefinition = "TEXT")
    private String missingKeywordsCsv;

    private Instant createdAt;

    public InterviewAttemptEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public InterviewSessionEntity getSession() { return session; }
    public void setSession(InterviewSessionEntity session) { this.session = session; }

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

    public String getMissingKeywordsCsv() { return missingKeywordsCsv; }
    public void setMissingKeywordsCsv(String missingKeywordsCsv) { this.missingKeywordsCsv = missingKeywordsCsv; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
