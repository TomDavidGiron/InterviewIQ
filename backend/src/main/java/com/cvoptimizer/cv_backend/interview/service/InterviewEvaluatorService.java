package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.ai.dto.AiEvaluationRequest;
import com.cvoptimizer.cv_backend.interview.ai.dto.AiEvaluationResponse;
import com.cvoptimizer.cv_backend.interview.ai.service.AiEvaluationService;
import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.interview.model.QuestionType;
import com.cvoptimizer.cv_backend.interview.rag.service.RagService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class InterviewEvaluatorService {

    private final RagService ragService;
    private final AiEvaluationService aiEvaluationService;

    private static final double QUESTION_PASS_RATIO = 0.60;
    private static final int MAX_KEYWORDS_FOR_SCORING = 4;
    private static final int RAG_LIMIT = 3;

    public InterviewEvaluatorService(RagService ragService, AiEvaluationService aiEvaluationService) {
        this.ragService = ragService;
        this.aiEvaluationService = aiEvaluationService;
    }

    public EvaluationResult evaluate(InterviewQuestion question, String answerText) {

        if (question == null) {
            return EvaluationResult.fail("Invalid question", List.of(), 0, 1, List.of(), null, "keyword");
        }

        QuestionType type = question.getType();
        if (type == null) type = QuestionType.OPEN;

        if (type == QuestionType.MCQ) {
            return evalMcq(question, answerText);
        }

        String ans = normalize(answerText);
        Set<String> reqSet = question.getRequiredKeywords();
        List<String> req = (reqSet == null) ? List.of() : new ArrayList<>(reqSet);

        if (req.isEmpty()) {
            if (type == QuestionType.CODE && ans.isBlank()) {
                return EvaluationResult.fail("Empty answer", List.of(), 0, 1, List.of(), null, "keyword");
            }
            return EvaluationResult.pass(1, 1, List.of(), "Basic answer accepted.", "keyword");
        }

        req.sort(String.CASE_INSENSITIVE_ORDER);
        List<String> rubric = req.subList(0, Math.min(MAX_KEYWORDS_FOR_SCORING, req.size()));
        int maxPoints = rubric.size();

        if (ans.isBlank()) {
            return EvaluationResult.fail("Empty answer", rubric, 0, maxPoints, List.of(), null, "keyword");
        }

        List<String> ragContext = ragService.retrieveContext(question.getText(), RAG_LIMIT);

        Optional<AiEvaluationResponse> aiResult = aiEvaluationService.evaluate(
                new AiEvaluationRequest(
                        question.getText(),
                        rubric,
                        answerText == null ? "" : answerText,
                        ragContext
                )
        );

        if (aiResult.isPresent()) {
            return mapAiResult(aiResult.get(), maxPoints, rubric);
        }

        return evaluateWithKeywords(ans, rubric, maxPoints);
    }

    private EvaluationResult mapAiResult(AiEvaluationResponse ai, int maxPoints, List<String> fallbackRubric) {
        int normalizedScore = Math.max(0, Math.min(10, ai.getScore()));
        int earnedPoints = Math.max(0, Math.min(maxPoints, (int) Math.round((normalizedScore / 10.0) * maxPoints)));
        boolean passed = normalizedScore >= 6;

        List<String> missing = safeList(ai.getMissingConcepts());
        if (missing.isEmpty() && !passed) {
            missing = fallbackRubric;
        }

        List<String> strengths = safeList(ai.getStrengths());
        String feedback = blankToNull(ai.getFeedback());

        if (passed) {
            return EvaluationResult.pass(earnedPoints, maxPoints, strengths, feedback, "ai");
        }

        String reason = feedback != null
                ? feedback
                : "Missing key concept(s). Expected something like: " + String.join(", ", fallbackRubric);

        return EvaluationResult.fail(reason, missing, earnedPoints, maxPoints, strengths, feedback, "ai");
    }

    private EvaluationResult evaluateWithKeywords(String normalizedAnswer, List<String> rubric, int maxPoints) {
        int hitCount = 0;
        List<String> missing = new ArrayList<>();
        List<String> strengths = new ArrayList<>();

        for (String rawKeyword : rubric) {
            if (rawKeyword == null) continue;

            List<String> atoms = expandKeywordAtoms(rawKeyword);
            boolean found = false;

            for (String atom : atoms) {
                if (atom.length() < 2) continue;
                if (containsWord(normalizedAnswer, atom)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                hitCount++;
                strengths.add("Covered concept: " + rawKeyword);
            } else {
                missing.add(rawKeyword);
            }
        }

        int earned = hitCount;
        boolean passed = ((double) earned / (double) maxPoints) >= QUESTION_PASS_RATIO;
        String feedback = passed
                ? "Good coverage of the main concepts."
                : "Missing key concept(s). Expected something like: " + String.join(", ", rubric);

        if (passed) {
            return EvaluationResult.pass(earned, maxPoints, strengths, feedback, "keyword");
        }

        return EvaluationResult.fail(feedback, missing, earned, maxPoints, strengths, feedback, "keyword");
    }

    private EvaluationResult evalMcq(InterviewQuestion question, String answerText) {
        List<String> options = question.getOptions();
        Integer correctIdx = question.getCorrectOptionIndex();

        if (options == null || options.isEmpty() || correctIdx == null) {
            return EvaluationResult.fail("Invalid MCQ config", List.of(), 0, 1, List.of(), null, "keyword");
        }

        String raw = (answerText == null) ? "" : answerText.trim();
        if (raw.isBlank()) {
            return EvaluationResult.fail("Empty answer", List.of(), 0, 1, List.of(), null, "keyword");
        }

        Integer chosenIdx = null;
        if (raw.matches("\\d+")) {
            chosenIdx = Integer.parseInt(raw);
        } else if (raw.length() == 1 && Character.isLetter(raw.charAt(0))) {
            char c = Character.toUpperCase(raw.charAt(0));
            chosenIdx = (int) (c - 'A');
        }

        if (chosenIdx == null || chosenIdx < 0 || chosenIdx >= options.size()) {
            return EvaluationResult.fail("Invalid choice", List.of(), 0, 1, List.of(), null, "keyword");
        }

        boolean passed = Objects.equals(chosenIdx, correctIdx);
        if (passed) {
            return EvaluationResult.pass(1, 1, List.of("Selected the correct option."), "Correct answer.", "keyword");
        }

        String correctText = (correctIdx >= 0 && correctIdx < options.size()) ? options.get(correctIdx) : "(unknown)";

        return EvaluationResult.fail(
                "Wrong answer. Correct is: " + toLetter(correctIdx) + ". " + correctText,
                List.of(),
                0,
                1,
                List.of(),
                "Review why the correct option is " + toLetter(correctIdx) + ".",
                "keyword"
        );
    }

    private String toLetter(int idx) {
        if (idx < 0) return "?";
        return String.valueOf((char) ('A' + idx));
    }

    private List<String> expandKeywordAtoms(String raw) {
        String normalized = normalize(raw);
        String[] parts = normalized.split("[,\\s]+");
        List<String> out = new ArrayList<>();

        for (String p : parts) {
            String t = p.trim();
            if (!t.isBlank()) out.add(t);
        }

        if (out.isEmpty() && !normalized.isBlank()) {
            out.add(normalized);
        }

        return out;
    }

    private String normalize(String s) {
        return (s == null ? "" : s)
                .toLowerCase()
                .replaceAll("[^a-z0-9_\\s,]", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    private boolean containsWord(String normalizedAnswer, String keyword) {
        String pattern = "\\b" + Pattern.quote(keyword) + "\\b";
        return Pattern.compile(pattern).matcher(normalizedAnswer).find();
    }

    private List<String> safeList(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public static class EvaluationResult {
        private final boolean passed;
        private final String reason;
        private final List<String> missingKeywords;
        private final int earnedPoints;
        private final int maxPoints;
        private final List<String> strengths;
        private final String feedback;
        private final String evaluationSource;

        private EvaluationResult(
                boolean passed,
                String reason,
                List<String> missingKeywords,
                int earnedPoints,
                int maxPoints,
                List<String> strengths,
                String feedback,
                String evaluationSource
        ) {
            this.passed = passed;
            this.reason = reason;
            this.missingKeywords = missingKeywords;
            this.earnedPoints = earnedPoints;
            this.maxPoints = maxPoints;
            this.strengths = strengths == null ? List.of() : List.copyOf(strengths);
            this.feedback = feedback;
            this.evaluationSource = evaluationSource;
        }

        public static EvaluationResult pass(
                int earnedPoints,
                int maxPoints,
                List<String> strengths,
                String feedback,
                String evaluationSource
        ) {
            return new EvaluationResult(true, null, List.of(), earnedPoints, maxPoints, strengths, feedback, evaluationSource);
        }

        public static EvaluationResult fail(
                String reason,
                List<String> missingKeywords,
                int earnedPoints,
                int maxPoints,
                List<String> strengths,
                String feedback,
                String evaluationSource
        ) {
            return new EvaluationResult(false, reason, missingKeywords, earnedPoints, maxPoints, strengths, feedback, evaluationSource);
        }

        public boolean isPassed() { return passed; }
        public String getReason() { return reason; }
        public List<String> getMissingKeywords() { return missingKeywords; }
        public int getEarnedPoints() { return earnedPoints; }
        public int getMaxPoints() { return maxPoints; }
        public List<String> getStrengths() { return strengths; }
        public String getFeedback() { return feedback; }
        public String getEvaluationSource() { return evaluationSource; }
    }
}
