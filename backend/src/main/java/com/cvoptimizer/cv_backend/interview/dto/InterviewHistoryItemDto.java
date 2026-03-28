package com.cvoptimizer.cv_backend.interview.dto;

import java.time.Instant;

public class InterviewHistoryItemDto {

    private String sessionId;
    private String roleHint;
    private String levelHint;
    private String status;
    private Instant createdAt;
    private Instant endedAt;
    private int score;
    private int totalPossibleScore;
    private String failReason;

    public InterviewHistoryItemDto() {}

    public InterviewHistoryItemDto(
            String sessionId,
            String roleHint,
            String levelHint,
            String status,
            Instant createdAt,
            Instant endedAt,
            int score,
            int totalPossibleScore,
            String failReason
    ) {
        this.sessionId = sessionId;
        this.roleHint = roleHint;
        this.levelHint = levelHint;
        this.status = status;
        this.createdAt = createdAt;
        this.endedAt = endedAt;
        this.score = score;
        this.totalPossibleScore = totalPossibleScore;
        this.failReason = failReason;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getRoleHint() { return roleHint; }
    public void setRoleHint(String roleHint) { this.roleHint = roleHint; }

    public String getLevelHint() { return levelHint; }
    public void setLevelHint(String levelHint) { this.levelHint = levelHint; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getEndedAt() { return endedAt; }
    public void setEndedAt(Instant endedAt) { this.endedAt = endedAt; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalPossibleScore() { return totalPossibleScore; }
    public void setTotalPossibleScore(int totalPossibleScore) { this.totalPossibleScore = totalPossibleScore; }

    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }
}
