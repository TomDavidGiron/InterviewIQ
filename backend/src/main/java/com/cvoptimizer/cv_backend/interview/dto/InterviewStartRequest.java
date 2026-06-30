package com.cvoptimizer.cv_backend.interview.dto;

import jakarta.validation.constraints.Size;

public class InterviewStartRequest {

    private InterviewSourceType sourceType;

    @Size(max = 50_000, message = "payload must be at most 50000 characters")
    private String payload;

    @Size(max = 100, message = "topic must be at most 100 characters")
    private String topic;

    @Size(max = 100, message = "userId must be at most 100 characters")
    private String userId;

    public InterviewSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(InterviewSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}