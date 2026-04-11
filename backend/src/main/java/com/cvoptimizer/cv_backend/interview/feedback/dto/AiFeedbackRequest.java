package com.cvoptimizer.cv_backend.interview.feedback.dto;

import java.util.List;
import java.util.Map;

public class AiFeedbackRequest {

    private int overallScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> weakTopics;
    private List<Map<String, Object>> tagBreakdown;
    private List<String> criticalFailures;
    private Map<String, Integer> skillGraph;
    private List<FeedbackAttemptDto> attempts;

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public List<String> getWeakTopics() { return weakTopics; }
    public void setWeakTopics(List<String> weakTopics) { this.weakTopics = weakTopics; }

    public List<Map<String, Object>> getTagBreakdown() { return tagBreakdown; }
    public void setTagBreakdown(List<Map<String, Object>> tagBreakdown) { this.tagBreakdown = tagBreakdown; }

    public List<String> getCriticalFailures() { return criticalFailures; }
    public void setCriticalFailures(List<String> criticalFailures) { this.criticalFailures = criticalFailures; }

    public Map<String, Integer> getSkillGraph() { return skillGraph; }
    public void setSkillGraph(Map<String, Integer> skillGraph) { this.skillGraph = skillGraph; }

    public List<FeedbackAttemptDto> getAttempts() { return attempts; }
    public void setAttempts(List<FeedbackAttemptDto> attempts) { this.attempts = attempts; }
}