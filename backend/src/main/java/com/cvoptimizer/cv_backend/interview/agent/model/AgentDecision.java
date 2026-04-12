package com.cvoptimizer.cv_backend.interview.agent.model;

import java.util.HashMap;
import java.util.Map;

public class AgentDecision {

    private AgentAction action;
    private String reason;
    private String nextTopic;
    private Integer difficultyDelta;
    private String followUpPrompt;
    private String targetQuestionId;
    private boolean shouldUseRag;
    private Map<String, Object> metadata = new HashMap<>();

    public AgentDecision() {
    }

    public AgentDecision(AgentAction action, String reason) {
        this.action = action;
        this.reason = reason;
    }

    public static AgentDecision nextQuestion(String reason) {
        return new AgentDecision(AgentAction.ASK_NEXT_QUESTION, reason);
    }

    public static AgentDecision followUp(String reason, String followUpPrompt, boolean shouldUseRag) {
        AgentDecision decision = new AgentDecision(AgentAction.ASK_FOLLOW_UP, reason);
        decision.setFollowUpPrompt(followUpPrompt);
        decision.setShouldUseRag(shouldUseRag);
        return decision;
    }

    public static AgentDecision switchTopic(String reason, String nextTopic) {
        AgentDecision decision = new AgentDecision(AgentAction.SWITCH_TOPIC, reason);
        decision.setNextTopic(nextTopic);
        return decision;
    }

    public static AgentDecision finish(String reason) {
        return new AgentDecision(AgentAction.FINISH_INTERVIEW, reason);
    }

    public AgentAction getAction() {
        return action;
    }

    public void setAction(AgentAction action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNextTopic() {
        return nextTopic;
    }

    public void setNextTopic(String nextTopic) {
        this.nextTopic = nextTopic;
    }

    public Integer getDifficultyDelta() {
        return difficultyDelta;
    }

    public void setDifficultyDelta(Integer difficultyDelta) {
        this.difficultyDelta = difficultyDelta;
    }

    public String getFollowUpPrompt() {
        return followUpPrompt;
    }

    public void setFollowUpPrompt(String followUpPrompt) {
        this.followUpPrompt = followUpPrompt;
    }

    public String getTargetQuestionId() {
        return targetQuestionId;
    }

    public void setTargetQuestionId(String targetQuestionId) {
        this.targetQuestionId = targetQuestionId;
    }

    public boolean isShouldUseRag() {
        return shouldUseRag;
    }

    public void setShouldUseRag(boolean shouldUseRag) {
        this.shouldUseRag = shouldUseRag;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}