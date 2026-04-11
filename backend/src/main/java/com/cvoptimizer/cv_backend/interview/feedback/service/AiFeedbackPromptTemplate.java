package com.cvoptimizer.cv_backend.interview.feedback.service;

import com.cvoptimizer.cv_backend.interview.feedback.dto.AiFeedbackRequest;

public class AiFeedbackPromptTemplate {

    public static String buildPrompt(AiFeedbackRequest req) {

        return """
You are an expert technical interview coach.

Analyze the interview performance and return a JSON with:
- diagnosis
- strengths
- weaknesses
- studyPlan
- feedbackSummary

Score: %d
Weak Topics: %s
Strengths: %s
Weaknesses: %s

Be concise but insightful.
""".formatted(
                req.getOverallScore(),
                req.getWeakTopics(),
                req.getStrengths(),
                req.getWeaknesses()
        );
    }
}