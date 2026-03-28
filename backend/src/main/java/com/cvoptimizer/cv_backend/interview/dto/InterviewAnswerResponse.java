package com.cvoptimizer.cv_backend.interview.dto;

import com.cvoptimizer.cv_backend.interview.model.InterviewStatus;

import java.util.List;
import java.util.Map;

public class InterviewAnswerResponse {
    private InterviewStatus status;
    private QuestionDto question;          // next question (if IN_PROGRESS)
    private String failReason;             // if FAILED (or feedback for non-critical misses if you want)
    private int questionIndex;             // 0-based
    private int totalQuestions;
    private QuestionDto currentQuestion;   // the question that was just evaluated (or last evaluated if session ended)

    // scoring + feedback
    private Integer scoreSoFar;
    private Integer maxScoreSoFar;
    private List<String> missingKeywords;
    private List<String> strengths;
    private String feedback;
    private String evaluationSource;
    private Map<String, Integer> weakTags;

    // NEW: agent trace
    private String agentAction;
    private String agentReason;

    public InterviewAnswerResponse() {}

    // Keep existing constructor
    public InterviewAnswerResponse(InterviewStatus status,
                                   QuestionDto question,
                                   String failReason,
                                   int questionIndex,
                                   int totalQuestions,
                                   QuestionDto currentQuestion) {
        this.status = status;
        this.question = question;
        this.failReason = failReason;
        this.questionIndex = questionIndex;
        this.totalQuestions = totalQuestions;
        this.currentQuestion = currentQuestion;
    }

    // Extended constructor
    public InterviewAnswerResponse(InterviewStatus status,
                                   QuestionDto question,
                                   String failReason,
                                   int questionIndex,
                                   int totalQuestions,
                                   QuestionDto currentQuestion,
                                   Integer scoreSoFar,
                                   Integer maxScoreSoFar,
                                   List<String> missingKeywords,
                                   List<String> strengths,
                                   String feedback,
                                   String evaluationSource,
                                   Map<String, Integer> weakTags) {
        this.status = status;
        this.question = question;
        this.failReason = failReason;
        this.questionIndex = questionIndex;
        this.totalQuestions = totalQuestions;
        this.currentQuestion = currentQuestion;
        this.scoreSoFar = scoreSoFar;
        this.maxScoreSoFar = maxScoreSoFar;
        this.missingKeywords = missingKeywords;
        this.strengths = strengths;
        this.feedback = feedback;
        this.evaluationSource = evaluationSource;
        this.weakTags = weakTags;
    }

    public InterviewStatus getStatus() { return status; }
    public void setStatus(InterviewStatus status) { this.status = status; }

    public QuestionDto getQuestion() { return question; }
    public void setQuestion(QuestionDto question) { this.question = question; }

    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }

    public int getQuestionIndex() { return questionIndex; }
    public void setQuestionIndex(int questionIndex) { this.questionIndex = questionIndex; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public QuestionDto getCurrentQuestion() { return currentQuestion; }
    public void setCurrentQuestion(QuestionDto currentQuestion) { this.currentQuestion = currentQuestion; }

    public Integer getScoreSoFar() { return scoreSoFar; }
    public void setScoreSoFar(Integer scoreSoFar) { this.scoreSoFar = scoreSoFar; }

    public Integer getMaxScoreSoFar() { return maxScoreSoFar; }
    public void setMaxScoreSoFar(Integer maxScoreSoFar) { this.maxScoreSoFar = maxScoreSoFar; }

    public List<String> getMissingKeywords() { return missingKeywords; }
    public void setMissingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getEvaluationSource() { return evaluationSource; }
    public void setEvaluationSource(String evaluationSource) { this.evaluationSource = evaluationSource; }

    public Map<String, Integer> getWeakTags() { return weakTags; }
    public void setWeakTags(Map<String, Integer> weakTags) { this.weakTags = weakTags; }

    public String getAgentAction() { return agentAction; }
    public void setAgentAction(String agentAction) { this.agentAction = agentAction; }

    public String getAgentReason() { return agentReason; }
    public void setAgentReason(String agentReason) { this.agentReason = agentReason; }
}