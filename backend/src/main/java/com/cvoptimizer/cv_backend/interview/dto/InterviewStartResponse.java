package com.cvoptimizer.cv_backend.interview.dto;

public class InterviewStartResponse {
    private String sessionId;
    private QuestionDto question;

    public InterviewStartResponse() {}

    public InterviewStartResponse(String sessionId, QuestionDto question) {
        this.sessionId = sessionId;
        this.question = question;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public QuestionDto getQuestion() { return question; }
    public void setQuestion(QuestionDto question) { this.question = question; }
}
