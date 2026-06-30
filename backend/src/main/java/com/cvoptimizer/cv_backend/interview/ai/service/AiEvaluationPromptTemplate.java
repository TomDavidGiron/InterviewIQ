package com.cvoptimizer.cv_backend.interview.ai.service;

import com.cvoptimizer.cv_backend.interview.ai.dto.AiEvaluationRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AiEvaluationPromptTemplate {

    public String buildDeveloperPrompt() {
        return """
                You are a friendly, encouraging technical interview coach.
                Evaluate the candidate's answer for concept understanding — not perfect wording.
                The candidate may answer informally or briefly. That is fine.
                Give partial credit generously when the core idea is correct even if details are missing.
                Score 0–10: 0 = no understanding, 5 = partial understanding, 8+ = solid grasp, 10 = complete.
                Feedback must be short, warm, and actionable — like a mentor, not a judge.
                Never penalize informal language or incomplete sentences.
                Only list a concept as missing if it is truly absent, not just stated differently.
                Return JSON only.
                """;
    }

    public String buildUserPrompt(AiEvaluationRequest request) {
        String concepts = formatList(request.getExpectedConcepts());
        String ragContext = formatContext(request.getRagContext());
        String answer = sanitizeMultiline(request.getUserAnswer());
        String question = sanitizeMultiline(request.getQuestion());

        return """
                Evaluate this interview answer. Be lenient — focus on whether the candidate understands the concept.

                QUESTION:
                %s

                KEY_CONCEPTS_TO_COVER:
                %s

                CANDIDATE_ANSWER:
                %s

                REFERENCE_CONTEXT:
                %s

                Return a JSON object with exactly these fields:
                {
                  "score": 0,
                  "missingConcepts": [],
                  "strengths": [],
                  "feedback": ""
                }

                Rules:
                - score 0-10 (be generous for partial understanding)
                - missingConcepts: only truly absent concepts, max 3 items
                - strengths: what the candidate got right, at least 1 item if score > 0
                - feedback: 1-2 sentences, encouraging, tells them what to study next
                """.formatted(question, concepts, answer, ragContext);
    }

    private String formatList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        return values.stream()
                .map(v -> "- " + sanitizeInline(v))
                .collect(Collectors.joining("\n"));
    }

    private String formatContext(List<String> context) {
        if (context == null || context.isEmpty()) {
            return "No additional context.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < context.size(); i++) {
            sb.append("[Context ").append(i + 1).append("]\n");
            sb.append(sanitizeMultiline(context.get(i))).append("\n\n");
        }
        return sb.toString().trim();
    }

    private String sanitizeInline(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

    private String sanitizeMultiline(String value) {
        return value == null ? "" : value.trim();
    }
}
