package com.cvoptimizer.cv_backend.interview.model;

import com.cvoptimizer.cv_backend.interview.agent.model.AgentAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InterviewSession {

    private String sessionId;
    private String userId;
    private String topic;

    private List<InterviewQuestion> questions = new ArrayList<>();
    private int currentIndex;

    // Existing engine-compatible fields
    private int score;
    private int totalPossibleScore;
    private String failReason;
    private List<String> answers = new ArrayList<>();
    private Set<String> extractedSkills = new LinkedHashSet<>();
    private Map<String, Integer> tagMistakes = new HashMap<>();
    private InterviewQuestion lastEvaluatedQuestion;

    private InterviewStatus status = InterviewStatus.IN_PROGRESS;

    // Legacy / additional fields already introduced
    private int totalScore;
    private int maxScore;
    private List<String> weakTopics = new ArrayList<>();
    private List<String> strengths = new ArrayList<>();
    private List<String> weaknesses = new ArrayList<>();

    private int currentDifficulty = 2;
    private int consecutivePasses = 0;
    private int consecutiveFails = 0;
    private int followUpCount = 0;
    private AgentAction lastAgentAction;
    private long createdAtEpochMs;
    private long lastAccessEpochMs;

    // Phase 6
    private boolean jobSpecific;
    private String jobDescriptionText;
    private List<String> prioritizedJobSkills = new ArrayList<>();
    private List<String> mustHaveJobSkills = new ArrayList<>();
    private List<String> niceToHaveJobSkills = new ArrayList<>();
    private String targetRoleHint;
    private String targetSeniorityHint;
    private String targetDomainHint;
    private boolean generatedQuestionsUsed;

    public InterviewSession() {
    }

    public InterviewSession(String sessionId, List<InterviewQuestion> questions, Set<String> extractedSkills) {
        this.sessionId = sessionId;
        this.questions = questions != null ? new ArrayList<>(questions) : new ArrayList<>();
        this.extractedSkills = extractedSkills != null ? new LinkedHashSet<>(extractedSkills) : new LinkedHashSet<>();
        this.currentIndex = 0;
        this.score = 0;
        this.totalPossibleScore = 0;
        this.status = InterviewStatus.IN_PROGRESS;
        this.answers = new ArrayList<>();
        this.tagMistakes = new HashMap<>();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<InterviewQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<InterviewQuestion> questions) {
        this.questions = questions != null ? questions : new ArrayList<>();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
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

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers != null ? answers : new ArrayList<>();
    }

    public Set<String> getExtractedSkills() {
        return extractedSkills;
    }

    public void setExtractedSkills(Set<String> extractedSkills) {
        this.extractedSkills = extractedSkills != null ? extractedSkills : new LinkedHashSet<>();
    }

    public Map<String, Integer> getTagMistakes() {
        return tagMistakes;
    }

    public void setTagMistakes(Map<String, Integer> tagMistakes) {
        this.tagMistakes = tagMistakes != null ? tagMistakes : new HashMap<>();
    }

    public InterviewQuestion getLastEvaluatedQuestion() {
        return lastEvaluatedQuestion;
    }

    public void setLastEvaluatedQuestion(InterviewQuestion lastEvaluatedQuestion) {
        this.lastEvaluatedQuestion = lastEvaluatedQuestion;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public List<String> getWeakTopics() {
        return weakTopics;
    }

    public void setWeakTopics(List<String> weakTopics) {
        this.weakTopics = weakTopics != null ? weakTopics : new ArrayList<>();
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths != null ? strengths : new ArrayList<>();
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses != null ? weaknesses : new ArrayList<>();
    }

    public int getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(int currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public int getConsecutivePasses() {
        return consecutivePasses;
    }

    public void setConsecutivePasses(int consecutivePasses) {
        this.consecutivePasses = consecutivePasses;
    }

    public int getConsecutiveFails() {
        return consecutiveFails;
    }

    public void setConsecutiveFails(int consecutiveFails) {
        this.consecutiveFails = consecutiveFails;
    }

    public int getFollowUpCount() {
        return followUpCount;
    }

    public void setFollowUpCount(int followUpCount) {
        this.followUpCount = followUpCount;
    }

    public AgentAction getLastAgentAction() {
        return lastAgentAction;
    }

    public void setLastAgentAction(AgentAction lastAgentAction) {
        this.lastAgentAction = lastAgentAction;
    }

    public boolean isJobSpecific() {
        return jobSpecific;
    }

    public void setJobSpecific(boolean jobSpecific) {
        this.jobSpecific = jobSpecific;
    }

    public String getJobDescriptionText() {
        return jobDescriptionText;
    }

    public void setJobDescriptionText(String jobDescriptionText) {
        this.jobDescriptionText = jobDescriptionText;
    }

    public List<String> getPrioritizedJobSkills() {
        return prioritizedJobSkills;
    }

    public void setPrioritizedJobSkills(List<String> prioritizedJobSkills) {
        this.prioritizedJobSkills = prioritizedJobSkills != null ? prioritizedJobSkills : new ArrayList<>();
    }

    public List<String> getMustHaveJobSkills() {
        return mustHaveJobSkills;
    }

    public void setMustHaveJobSkills(List<String> mustHaveJobSkills) {
        this.mustHaveJobSkills = mustHaveJobSkills != null ? mustHaveJobSkills : new ArrayList<>();
    }

    public List<String> getNiceToHaveJobSkills() {
        return niceToHaveJobSkills;
    }

    public void setNiceToHaveJobSkills(List<String> niceToHaveJobSkills) {
        this.niceToHaveJobSkills = niceToHaveJobSkills != null ? niceToHaveJobSkills : new ArrayList<>();
    }

    public String getTargetRoleHint() {
        return targetRoleHint;
    }

    public void setTargetRoleHint(String targetRoleHint) {
        this.targetRoleHint = targetRoleHint;
    }

    public String getTargetSeniorityHint() {
        return targetSeniorityHint;
    }

    public void setTargetSeniorityHint(String targetSeniorityHint) {
        this.targetSeniorityHint = targetSeniorityHint;
    }

    public String getTargetDomainHint() {
        return targetDomainHint;
    }

    public void setTargetDomainHint(String targetDomainHint) {
        this.targetDomainHint = targetDomainHint;
    }

    public boolean isGeneratedQuestionsUsed() {
        return generatedQuestionsUsed;
    }

    public void setGeneratedQuestionsUsed(boolean generatedQuestionsUsed) {
        this.generatedQuestionsUsed = generatedQuestionsUsed;
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
}