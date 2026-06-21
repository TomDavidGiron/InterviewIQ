package com.cvoptimizer.cv_backend.interview.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "interview_session")
public class InterviewSessionEntity {

    @Id
    @Column(length = 36)
    private String id;

    private String roleHint;
    private String levelHint;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Column(columnDefinition = "TEXT")
    private String failReason;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    private Integer score;
    private Integer totalPossibleScore;

    private Instant createdAt;
    private Instant endedAt;

    @Column(columnDefinition = "TEXT")
    private String sessionState;

    public enum SessionStatus {
        IN_PROGRESS,
        PASSED,
        FAILED
    }

    public InterviewSessionEntity() {
    }

    public InterviewSessionEntity(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoleHint() { return roleHint; }
    public void setRoleHint(String roleHint) { this.roleHint = roleHint; }

    public String getLevelHint() { return levelHint; }
    public void setLevelHint(String levelHint) { this.levelHint = levelHint; }

    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }

    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getTotalPossibleScore() { return totalPossibleScore; }
    public void setTotalPossibleScore(Integer totalPossibleScore) { this.totalPossibleScore = totalPossibleScore; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getEndedAt() { return endedAt; }
    public void setEndedAt(Instant endedAt) { this.endedAt = endedAt; }

    public String getSessionState() { return sessionState; }
    public void setSessionState(String sessionState) { this.sessionState = sessionState; }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
