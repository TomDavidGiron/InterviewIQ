package com.cvoptimizer.cv_backend.interview.feedback.dto;

import java.util.List;

public class FeedbackAttemptDto {

    private String question;
    private String userAnswer;
    private int score;
    private List<String> missingConcepts;

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public List<String> getMissingConcepts() { return missingConcepts; }
    public void setMissingConcepts(List<String> missingConcepts) { this.missingConcepts = missingConcepts; }
}