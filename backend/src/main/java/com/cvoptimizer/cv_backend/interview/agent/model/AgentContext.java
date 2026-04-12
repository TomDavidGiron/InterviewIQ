package com.cvoptimizer.cv_backend.interview.agent.model;

import com.cvoptimizer.cv_backend.interview.dto.InterviewAttemptDto;
import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgentContext {

    private String sessionId;
    private InterviewQuestion currentQuestion;
    private String userAnswer;

    private int questionIndex;
    private int totalQuestions;

    private int scoreSoFar;
    private int maxScoreSoFar;
    private int currentPercent;

    private boolean lastEvaluationPassed;
    private List<String> missingConcepts = new ArrayList<>();
    private List<String> strengths = new ArrayList<>();
    private String feedback;

    private Set<String> weakTopics = new HashSet<>();
    private Set<String> extractedSkills = new HashSet<>();

    private List<InterviewAttemptDto> history = new ArrayList<>();

    private boolean jobSpecificMode;

    // Phase 3 advanced state
    private int currentDifficulty;
    private int consecutivePasses;
    private int consecutiveFails;
    private int followUpCount;
    private AgentAction lastAgentAction;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public InterviewQuestion getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(InterviewQuestion currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getScoreSoFar() {
        return scoreSoFar;
    }

    public void setScoreSoFar(int scoreSoFar) {
        this.scoreSoFar = scoreSoFar;
    }

    public int getMaxScoreSoFar() {
        return maxScoreSoFar;
    }

    public void setMaxScoreSoFar(int maxScoreSoFar) {
        this.maxScoreSoFar = maxScoreSoFar;
    }

    public int getCurrentPercent() {
        return currentPercent;
    }

    public void setCurrentPercent(int currentPercent) {
        this.currentPercent = currentPercent;
    }

    public boolean isLastEvaluationPassed() {
        return lastEvaluationPassed;
    }

    public void setLastEvaluationPassed(boolean lastEvaluationPassed) {
        this.lastEvaluationPassed = lastEvaluationPassed;
    }

    public List<String> getMissingConcepts() {
        return missingConcepts;
    }

    public void setMissingConcepts(List<String> missingConcepts) {
        this.missingConcepts = missingConcepts == null ? new ArrayList<>() : missingConcepts;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths == null ? new ArrayList<>() : strengths;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Set<String> getWeakTopics() {
        return weakTopics;
    }

    public void setWeakTopics(Set<String> weakTopics) {
        this.weakTopics = weakTopics == null ? new HashSet<>() : weakTopics;
    }

    public Set<String> getExtractedSkills() {
        return extractedSkills;
    }

    public void setExtractedSkills(Set<String> extractedSkills) {
        this.extractedSkills = extractedSkills == null ? new HashSet<>() : extractedSkills;
    }

    public List<InterviewAttemptDto> getHistory() {
        return history;
    }

    public void setHistory(List<InterviewAttemptDto> history) {
        this.history = history == null ? new ArrayList<>() : history;
    }

    public boolean isJobSpecificMode() {
        return jobSpecificMode;
    }

    public void setJobSpecificMode(boolean jobSpecificMode) {
        this.jobSpecificMode = jobSpecificMode;
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
}