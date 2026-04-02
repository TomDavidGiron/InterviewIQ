package com.cvoptimizer.cv_backend.interview.ai.dto;

import java.util.ArrayList;
import java.util.List;

public class AiEvaluationResponse {
    private int score;
    private List<String> missingConcepts;
    private List<String> strengths;
    private String feedback;

    public AiEvaluationResponse() {
        this.missingConcepts = new ArrayList<>();
        this.strengths = new ArrayList<>();
    }

    public AiEvaluationResponse(int score, List<String> missingConcepts, List<String> strengths, String feedback) {
        this.score = score;
        this.missingConcepts = missingConcepts == null ? new ArrayList<>() : new ArrayList<>(missingConcepts);
        this.strengths = strengths == null ? new ArrayList<>() : new ArrayList<>(strengths);
        this.feedback = feedback;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getMissingConcepts() {
        return missingConcepts;
    }

    public void setMissingConcepts(List<String> missingConcepts) {
        this.missingConcepts = missingConcepts == null ? new ArrayList<>() : new ArrayList<>(missingConcepts);
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths == null ? new ArrayList<>() : new ArrayList<>(strengths);
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
