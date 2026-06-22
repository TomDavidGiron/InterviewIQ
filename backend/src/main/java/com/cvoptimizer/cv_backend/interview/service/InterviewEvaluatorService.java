package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.ai.dto.AiEvaluationRequest;
import com.cvoptimizer.cv_backend.interview.ai.dto.AiEvaluationResponse;
import com.cvoptimizer.cv_backend.interview.ai.service.AiEvaluationService;
import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.interview.model.QuestionType;
import com.cvoptimizer.cv_backend.interview.rag.service.RagService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
        if (type == null) {
            type = QuestionType.OPEN;
        }

        if (type == QuestionType.MCQ) {
            return evalMcq(question, answerText);
        }

        String normalizedAnswer = normalize(answerText);
        List<String> rubric = buildRubric(question);

        if (rubric.isEmpty()) {
            if (type == QuestionType.CODE && normalizedAnswer.isBlank()) {
                return EvaluationResult.fail("Empty answer", List.of(), 0, 1, List.of(), null, "keyword");
            }
            return EvaluationResult.pass(1, 1, List.of(), "Basic answer accepted.", "keyword");
        }

        int maxPoints = rubric.size();
        if (normalizedAnswer.isBlank()) {
            return EvaluationResult.fail("Empty answer", rubric, 0, maxPoints, List.of(), null, "keyword");
        }

        KeywordMatchResult keywordResult = evaluateKeywordCoverage(normalizedAnswer, rubric, maxPoints);
        Optional<EvaluationResult> aiResult = tryAiEvaluation(question, answerText, rubric, maxPoints, keywordResult);
        return aiResult.orElse(keywordResult.toEvaluationResult());
    }

    private List<String> buildRubric(InterviewQuestion question) {
        Set<String> reqSet = question.getRequiredKeywords();
        List<String> req = (reqSet == null) ? List.of() : new ArrayList<>(reqSet);
        req.sort(String.CASE_INSENSITIVE_ORDER);
        return req.subList(0, Math.min(MAX_KEYWORDS_FOR_SCORING, req.size()));
    }

    private Optional<EvaluationResult> tryAiEvaluation(
            InterviewQuestion question,
            String answerText,
            List<String> rubric,
            int maxPoints,
            KeywordMatchResult keywordResult
    ) {
        List<String> ragContext = ragService.retrieveContext(question.getText(), RAG_LIMIT);

        Optional<AiEvaluationResponse> aiResult = aiEvaluationService.evaluate(
                new AiEvaluationRequest(
                        question.getText(),
                        rubric,
                        answerText == null ? "" : answerText,
                        ragContext
                )
        );

        if (aiResult.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapAiResult(aiResult.get(), maxPoints, rubric, keywordResult));
    }

    private EvaluationResult mapAiResult(
            AiEvaluationResponse ai,
            int maxPoints,
            List<String> fallbackRubric,
            KeywordMatchResult keywordResult
    ) {
        int aiScore = Math.max(0, Math.min(10, ai.getScore()));
        int aiEarnedPoints = Math.max(0, Math.min(maxPoints, (int) Math.round((aiScore / 10.0) * maxPoints)));

        int mergedEarnedPoints = Math.max(aiEarnedPoints, keywordResult.earnedPoints());
        boolean passed = ((double) mergedEarnedPoints / (double) maxPoints) >= QUESTION_PASS_RATIO;

        List<String> strengths = new ArrayList<>();
        strengths.addAll(safeList(ai.getStrengths()));
        strengths.addAll(keywordResult.strengths());
        strengths = dedupe(strengths);

        List<String> missing = filterStillMissing(safeList(ai.getMissingConcepts()), keywordResult.coveredRubricItems());
        if (missing.isEmpty()) {
            missing = new ArrayList<>(keywordResult.missingKeywords());
        }
        if (missing.isEmpty() && !passed) {
            missing = new ArrayList<>(fallbackRubric);
        }

        String feedback = blankToNull(ai.getFeedback());
        if (feedback == null) {
            feedback = passed
                    ? "Good coverage of the main concepts."
                    : "Missing key concept(s). Expected something like: " + String.join(", ", missing);
        }

        if (passed) {
            return EvaluationResult.pass(mergedEarnedPoints, maxPoints, strengths, feedback, "ai");
        }

        return EvaluationResult.fail(feedback, missing, mergedEarnedPoints, maxPoints, strengths, feedback, "ai");
    }

    private KeywordMatchResult evaluateKeywordCoverage(String normalizedAnswer, List<String> rubric, int maxPoints) {
        int hitCount = 0;
        List<String> missing = new ArrayList<>();
        List<String> strengths = new ArrayList<>();
        Set<String> covered = new LinkedHashSet<>();

        for (String rawKeyword : rubric) {
            if (rawKeyword == null || rawKeyword.isBlank()) {
                continue;
            }

            boolean found = matchesConcept(normalizedAnswer, rawKeyword);
            if (found) {
                hitCount++;
                covered.add(rawKeyword);
                strengths.add("Covered concept: " + rawKeyword);
            } else {
                missing.add(rawKeyword);
            }
        }

        boolean passed = ((double) hitCount / (double) maxPoints) >= QUESTION_PASS_RATIO;
        String feedback = passed
                ? "Good coverage of the main concepts."
                : "Missing key concept(s). Expected something like: " + String.join(", ", rubric);

        return new KeywordMatchResult(hitCount, maxPoints, covered, missing, strengths, feedback, passed);
    }

    private boolean matchesConcept(String normalizedAnswer, String rawKeyword) {
        for (String candidate : expandKeywordCandidates(rawKeyword)) {
            if (candidate.length() < 2) {
                continue;
            }
            if (containsPhrase(normalizedAnswer, candidate)) {
                return true;
            }
        }
        return false;
    }

    private List<String> expandKeywordCandidates(String rawKeyword) {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        String normalized = normalize(rawKeyword);
        if (normalized.isBlank()) {
            return List.of();
        }

        candidates.add(normalized);
        candidates.add(singularize(normalized));
        candidates.add(stemPhrase(normalized));

        String[] parts = normalized.split("[,\\s]+");
        for (String part : parts) {
            String token = part.trim();
            if (token.isBlank()) {
                continue;
            }
            candidates.add(token);
            candidates.add(singularize(token));
            candidates.add(stemToken(token));

            for (String synonym : synonymsFor(token)) {
                candidates.add(synonym);
                candidates.add(stemToken(synonym));
            }
        }

        return candidates.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private String normalize(String s) {
        return (s == null ? "" : s)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_\\s,]", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    private boolean containsPhrase(String normalizedAnswer, String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return false;
        }

        String normalizedCandidate = normalize(candidate);
        if (normalizedCandidate.contains(" ")) {
            String compactAnswer = " " + normalizedAnswer + " ";
            String compactCandidate = " " + normalizedCandidate + " ";
            if (compactAnswer.contains(compactCandidate)) {
                return true;
            }
        }

        String pattern = "\\b" + Pattern.quote(normalizedCandidate) + "\\b";
        if (Pattern.compile(pattern).matcher(normalizedAnswer).find()) {
            return true;
        }

        String stemmedAnswer = stemPhrase(normalizedAnswer);
        String stemmedCandidate = stemPhrase(normalizedCandidate);
        String stemPattern = "\\b" + Pattern.quote(stemmedCandidate) + "\\b";
        return Pattern.compile(stemPattern).matcher(stemmedAnswer).find();
    }

    private String singularize(String token) {
        if (token == null || token.isBlank()) {
            return "";
        }
        if (token.endsWith("ies") && token.length() > 3) {
            return token.substring(0, token.length() - 3) + "y";
        }
        if (token.endsWith("s") && !token.endsWith("ss") && token.length() > 3) {
            return token.substring(0, token.length() - 1);
        }
        return token;
    }

    private String stemPhrase(String text) {
        String normalized = normalize(text);
        if (normalized.isBlank()) {
            return "";
        }

        String[] parts = normalized.split("\\s+");
        List<String> stemmed = new ArrayList<>();

        for (String part : parts) {
            if (!part.isBlank()) {
                stemmed.add(stemToken(part));
            }
        }

        return String.join(" ", stemmed);
    }

    private String stemToken(String token) {
        String t = singularize(normalize(token));
        if (t.endsWith("ing") && t.length() > 5) {
            return t.substring(0, t.length() - 3);
        }
        if (t.endsWith("ed") && t.length() > 4) {
            return t.substring(0, t.length() - 2);
        }
        if (t.endsWith("ent") && t.length() > 5) {
            return t.substring(0, t.length() - 3);
        }
        if (t.endsWith("ency") && t.length() > 5) {
            return t.substring(0, t.length() - 4);
        }
        if (t.endsWith("tion") && t.length() > 5) {
            return t.substring(0, t.length() - 4);
        }
        if (t.endsWith("ity") && t.length() > 4) {
            return t.substring(0, t.length() - 3);
        }
        return t;
    }

    private List<String> synonymsFor(String token) {
        return switch (token) {
            case "retry", "retries" -> List.of("retry", "retries", "retried", "repeat request", "repeated request");
            case "idempotent", "idempotency" -> List.of("idempotent", "idempotency", "same effect", "same final state");
            case "create" -> List.of("created", "creation");
            case "update" -> List.of("updated", "updates");
            case "delete" -> List.of("deleted", "removal", "remove");
            case "pagination" -> List.of("paging", "cursor", "offset", "limit");
            default -> List.of();
        };
    }

    private List<String> filterStillMissing(List<String> aiMissing, Set<String> coveredRubricItems) {
        if (aiMissing == null || aiMissing.isEmpty()) {
            return List.of();
        }

        List<String> filtered = new ArrayList<>();
        for (String item : aiMissing) {
            if (item == null || item.isBlank()) {
                continue;
            }

            boolean alreadyCovered = false;
            for (String covered : coveredRubricItems) {
                if (matchesConcept(normalize(item), covered) || matchesConcept(normalize(covered), item)) {
                    alreadyCovered = true;
                    break;
                }
            }

            if (!alreadyCovered) {
                filtered.add(item.trim());
            }
        }
        return dedupe(filtered);
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
        if (idx < 0) {
            return "?";
        }
        return String.valueOf((char) ('A' + idx));
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

    private List<String> dedupe(List<String> values) {
        return new ArrayList<>(new LinkedHashSet<>(safeList(values)));
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private record KeywordMatchResult(
            int earnedPoints,
            int maxPoints,
            Set<String> coveredRubricItems,
            List<String> missingKeywords,
            List<String> strengths,
            String feedback,
            boolean passed
    ) {
        private EvaluationResult toEvaluationResult() {
            if (passed) {
                return EvaluationResult.pass(earnedPoints, maxPoints, strengths, feedback, "keyword");
            }
            return EvaluationResult.fail(feedback, missingKeywords, earnedPoints, maxPoints, strengths, feedback, "keyword");
        }
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
