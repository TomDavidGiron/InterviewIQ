package com.cvoptimizer.cv_backend.interview.ai.dto;

import java.util.List;

public class AiEvaluationRequest {
    private final String question;
    private final List<String> expectedConcepts;
    private final String userAnswer;
    private final List<String> ragContext;

    public AiEvaluationRequest(
            String question,
            List<String> expectedConcepts,
            String userAnswer,
            List<String> ragContext
    ) {
        this.question = question;
        this.expectedConcepts = expectedConcepts == null ? List.of() : List.copyOf(expectedConcepts);
        this.userAnswer = userAnswer;
        this.ragContext = ragContext == null ? List.of() : List.copyOf(ragContext);
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getExpectedConcepts() {
        return expectedConcepts;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public List<String> getRagContext() {
        return ragContext;
    }
}
