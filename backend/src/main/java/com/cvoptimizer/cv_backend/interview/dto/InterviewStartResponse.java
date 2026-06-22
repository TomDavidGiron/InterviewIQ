package com.cvoptimizer.cv_backend.interview.dto;

public class InterviewStartResponse {

    private String sessionId;
    private QuestionDto firstQuestion;

    // Phase 6
    private boolean jobSpecific;
    private String roleHint;
    private String seniorityHint;
    private String domainHint;
    private java.util.List<String> prioritizedJobSkills;

    public InterviewStartResponse() {
    }

    public InterviewStartResponse(String sessionId, QuestionDto firstQuestion) {
        this.sessionId = sessionId;
        this.firstQuestion = firstQuestion;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public QuestionDto getFirstQuestion() {
        return firstQuestion;
    }

    public void setFirstQuestion(QuestionDto firstQuestion) {
        this.firstQuestion = firstQuestion;
    }

    public boolean isJobSpecific() {
        return jobSpecific;
    }

    public void setJobSpecific(boolean jobSpecific) {
        this.jobSpecific = jobSpecific;
    }

    public String getRoleHint() {
        return roleHint;
    }

    public void setRoleHint(String roleHint) {
        this.roleHint = roleHint;
    }

    public String getSeniorityHint() {
        return seniorityHint;
    }

    public void setSeniorityHint(String seniorityHint) {
        this.seniorityHint = seniorityHint;
    }

    public String getDomainHint() {
        return domainHint;
    }

    public void setDomainHint(String domainHint) {
        this.domainHint = domainHint;
    }

    public java.util.List<String> getPrioritizedJobSkills() {
        return prioritizedJobSkills;
    }

    public void setPrioritizedJobSkills(java.util.List<String> prioritizedJobSkills) {
        this.prioritizedJobSkills = prioritizedJobSkills;
    }
}