package com.cvoptimizer.cv_backend.interview.dto;

public class InterviewStartRequest {

    private InterviewSourceType sourceType;
    private String payload;
    private String topic;
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