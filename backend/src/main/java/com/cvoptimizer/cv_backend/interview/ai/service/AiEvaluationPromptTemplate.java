package com.cvoptimizer.cv_backend.interview.ai.service;

import com.cvoptimizer.cv_backend.interview.ai.dto.AiEvaluationRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AiEvaluationPromptTemplate {

    public String buildDeveloperPrompt() {
        return """
                You are an expert technical interview evaluator.
                Evaluate the candidate answer against the question, expected concepts, and optional retrieved knowledge context.
                Be strict but fair.
                Score from 0 to 10.
                Prefer concept understanding over exact wording.
                Do not invent missing concepts unless they are clearly absent.
                Keep strengths concise.
                Keep feedback concise, practical, and interview-oriented.
                Return JSON only.
                """;
    }

    public String buildUserPrompt(AiEvaluationRequest request) {
        String concepts = formatList(request.getExpectedConcepts());
        String ragContext = formatContext(request.getRagContext());
        String answer = sanitizeMultiline(request.getUserAnswer());
        String question = sanitizeMultiline(request.getQuestion());

        return """
                Evaluate this interview answer.

                QUESTION:
                %s

                EXPECTED_CONCEPTS:
                %s

                USER_ANSWER:
                %s

                RAG_CONTEXT:
                %s

                Return a JSON object with exactly these fields:
                {
                  "score": 0,
                  "missingConcepts": [],
                  "strengths": [],
                  "feedback": ""
                }
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
