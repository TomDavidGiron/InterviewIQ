package com.cvoptimizer.cv_backend.interview.model;

import com.cvoptimizer.cv_backend.interview.agent.model.AgentAction;

import java.util.*;

public class InterviewSession {

    private String sessionId;
    private List<InterviewQuestion> questions = new ArrayList<>();
    private Set<String> extractedSkills = new HashSet<>();
    private List<String> answers = new ArrayList<>();

    private int currentIndex = 0;
    private InterviewStatus status = InterviewStatus.IN_PROGRESS;
    private String failReason;

    private int score = 0;
    private int totalPossibleScore = 0;

    private Map<String, Integer> tagMistakes = new HashMap<>();
    private InterviewQuestion lastEvaluatedQuestion;

    // existing timestamps used by InterviewSessionStore
    private long createdAtEpochMs;
    private long lastAccessEpochMs;

    // Phase 3 agent state
    private int currentDifficulty = 2; // 1=easy, 2=medium, 3=hard
    private int consecutivePasses = 0;
    private int consecutiveFails = 0;
    private int followUpCount = 0;
    private AgentAction lastAgentAction;
    private String userId;

    public InterviewSession() {
        long now = System.currentTimeMillis();
        this.createdAtEpochMs = now;
        this.lastAccessEpochMs = now;
    }

    public InterviewSession(String sessionId, List<InterviewQuestion> questions, Set<String> extractedSkills) {
        this.sessionId = sessionId;
        this.questions = questions == null ? new ArrayList<>() : new ArrayList<>(questions);
        this.extractedSkills = extractedSkills == null ? new HashSet<>() : new HashSet<>(extractedSkills);
        this.answers = new ArrayList<>();
        this.tagMistakes = new HashMap<>();
        this.currentIndex = 0;
        this.status = InterviewStatus.IN_PROGRESS;
        this.currentDifficulty = 2;
        this.consecutivePasses = 0;
        this.consecutiveFails = 0;
        this.followUpCount = 0;

        long now = System.currentTimeMillis();
        this.createdAtEpochMs = now;
        this.lastAccessEpochMs = now;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<InterviewQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<InterviewQuestion> questions) {
        this.questions = questions == null ? new ArrayList<>() : questions;
    }

    public Set<String> getExtractedSkills() {
        return extractedSkills;
    }

    public void setExtractedSkills(Set<String> extractedSkills) {
        this.extractedSkills = extractedSkills == null ? new HashSet<>() : extractedSkills;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers == null ? new ArrayList<>() : answers;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalPossibleScore() {
        return totalPossibleScore;
    }

    public void setTotalPossibleScore(int totalPossibleScore) {
        this.totalPossibleScore = totalPossibleScore;
    }

    public Map<String, Integer> getTagMistakes() {
        return tagMistakes;
    }

    public void setTagMistakes(Map<String, Integer> tagMistakes) {
        this.tagMistakes = tagMistakes == null ? new HashMap<>() : tagMistakes;
    }

    public InterviewQuestion getLastEvaluatedQuestion() {
        return lastEvaluatedQuestion;
    }

    public void setLastEvaluatedQuestion(InterviewQuestion lastEvaluatedQuestion) {
        this.lastEvaluatedQuestion = lastEvaluatedQuestion;
    }

    public long getCreatedAtEpochMs() {
        return createdAtEpochMs;
    }

    public void setCreatedAtEpochMs(long createdAtEpochMs) {
        this.createdAtEpochMs = createdAtEpochMs;
    }

    public long getLastAccessEpochMs() {
        return lastAccessEpochMs;
    }

    public void setLastAccessEpochMs(long lastAccessEpochMs) {
        this.lastAccessEpochMs = lastAccessEpochMs;
    }

    public int getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(int currentDifficulty) {
        if (currentDifficulty < 1) currentDifficulty = 1;
        if (currentDifficulty > 3) currentDifficulty = 3;
        this.currentDifficulty = currentDifficulty;
    }

    public int getConsecutivePasses() {
        return consecutivePasses;
    }

    public void setConsecutivePasses(int consecutivePasses) {
        this.consecutivePasses = Math.max(0, consecutivePasses);
    }

    public int getConsecutiveFails() {
        return consecutiveFails;
    }

    public void setConsecutiveFails(int consecutiveFails) {
        this.consecutiveFails = Math.max(0, consecutiveFails);
    }

    public int getFollowUpCount() {
        return followUpCount;
    }

    public void setFollowUpCount(int followUpCount) {
        this.followUpCount = Math.max(0, followUpCount);
    }

    public AgentAction getLastAgentAction() {
        return lastAgentAction;
    }

    public void setLastAgentAction(AgentAction lastAgentAction) {
        this.lastAgentAction = lastAgentAction;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}