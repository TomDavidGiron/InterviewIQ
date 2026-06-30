package com.cvoptimizer.cv_backend.interview.dto;

import jakarta.validation.constraints.Size;

public class InterviewAnswerRequest {

    // Intentionally not @NotBlank — an empty answer is a valid "skipped question" state
    // that the engine scores as a miss, not a malformed request.
    @Size(max = 20_000, message = "answerText must be at most 20000 characters")
    private String answerText;

    public InterviewAnswerRequest() {}

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
}
