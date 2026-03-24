package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.dto.*;
import com.cvoptimizer.cv_backend.interview.model.*;
import com.cvoptimizer.cv_backend.model.ScraperResult;
import com.cvoptimizer.cv_backend.scraper.JobScraperRouter;
import com.cvoptimizer.cv_backend.service.JobSkillExtractorService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InterviewEngineService {

    private static final int QUESTIONS_PER_SESSION = 10;
    private static final int PASS_THRESHOLD_PERCENT = 70;

    private static final int TAG_PASS_THRESHOLD = 70;
    private static final int STRONG_THRESHOLD = 80;
    private static final int WEAK_THRESHOLD = 65;
    private static final int MIN_TAG_COUNT_FOR_STRENGTH_WEAKNESS = 2;
    private static final int CRIT_OVERALL_VERY_LOW = 40;

    private final JobSkillExtractorService skillExtractor;
    private final QuestionBankService questionBank;
    private final QuestionSelectorService selector;
    private final InterviewEvaluatorService evaluator;
    private final InterviewSessionStore store;
    private final InterviewHistoryService history;

    public InterviewEngineService(
            JobSkillExtractorService skillExtractor,
            QuestionBankService questionBank,
            QuestionSelectorService selector,
            InterviewEvaluatorService evaluator,
            InterviewSessionStore store,
            InterviewHistoryService history
    ) {
        this.skillExtractor = skillExtractor;
        this.questionBank = questionBank;
        this.selector = selector;
        this.evaluator = evaluator;
        this.store = store;
        this.history = history;
    }

    public InterviewStartResponse start(InterviewStartRequest request) {
        return startInternal(request, false);
    }

    public InterviewStartResponse startJobSpecificInterview(InterviewStartRequest request) {
        return startInternal(request, true);
    }

    private InterviewStartResponse startInternal(InterviewStartRequest request, boolean jobSpecificOnly) {
        if (request == null) {
            throw new IllegalArgumentException("Invalid start request");
        }

        String payload = request.getPayload() == null ? "" : request.getPayload().trim();
        InterviewSourceType sourceType = request.getSourceType();

        if (!payload.isBlank() && sourceType == null) {
            throw new IllegalArgumentException("Invalid start request: sourceType is required when payload is provided");
        }

        if (jobSpecificOnly && payload.isBlank()) {
            throw new IllegalArgumentException("Job-specific interview requires payload");
        }

        Set<String> skills;
        if (payload.isBlank()) {
            skills = Collections.emptySet();
        } else {
            String jobText = loadJobText(sourceType, payload);
            skills = skillExtractor.extractKeywords(jobText);
        }

        List<InterviewQuestion> bank = questionBank.getBackendJuniorBank();

        String topic = request.getTopic();
        if (topic != null && !topic.isBlank()) {
            List<InterviewQuestion> filtered = bank.stream()
                    .filter(q -> q.getTags() != null && q.getTags().contains(topic))
                    .toList();

            if (!filtered.isEmpty()) {
                bank = filtered;
            }
        }

        int n = Math.min(QUESTIONS_PER_SESSION, bank.size());
        List<InterviewQuestion> selected = selector.selectQuestions(skills, bank, n);

        String roleHint = buildRoleHint(skills);
        String levelHint = "junior";
        String sessionId = history.createSession(roleHint, levelHint).getId();

        InterviewSession session = new InterviewSession(sessionId, selected, skills);
        store.put(session);

        InterviewQuestion first = selected.isEmpty() ? null : selected.get(0);
        return new InterviewStartResponse(sessionId, toDto(first));
    }

    public InterviewAnswerResponse answer(String sessionId, InterviewAnswerRequest request) {
        InterviewSession session = store.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }

        if (session.getStatus() != InterviewStatus.IN_PROGRESS) {
            return new InterviewAnswerResponse(
                    session.getStatus(),
                    null,
                    session.getFailReason(),
                    session.getCurrentIndex(),
                    session.getQuestions().size(),
                    toDto(session.getLastEvaluatedQuestion()),
                    session.getScore(),
                    session.getTotalPossibleScore(),
                    List.of(),
                    List.of(),
                    null,
                    null,
                    safeCopyMap(session.getTagMistakes())
            );
        }

        int idx = session.getCurrentIndex();

        if (idx < 0 || idx >= session.getQuestions().size()) {
            finalizeSessionByThreshold(session);
            history.finalizeSession(sessionId, session.getStatus(), session.getFailReason());
            return new InterviewAnswerResponse(
                    session.getStatus(),
                    null,
                    session.getFailReason(),
                    idx,
                    session.getQuestions().size(),
                    toDto(session.getLastEvaluatedQuestion()),
                    session.getScore(),
                    session.getTotalPossibleScore(),
                    List.of(),
                    List.of(),
                    null,
                    null,
                    safeCopyMap(session.getTagMistakes())
            );
        }

        InterviewQuestion current = session.getQuestions().get(idx);
        session.setLastEvaluatedQuestion(current);

        String answerText = (request != null) ? request.getAnswerText() : null;
        session.getAnswers().add(answerText == null ? "" : answerText);

        InterviewEvaluatorService.EvaluationResult eval = evaluator.evaluate(current, answerText);

        history.appendAttempt(
                sessionId,
                current.getId(),
                current.getText(),
                answerText == null ? "" : answerText,
                firstNonBlank(eval.getFeedback(), eval.getReason()),
                eval.isPassed(),
                eval.getEarnedPoints(),
                eval.getMaxPoints(),
                eval.getMissingKeywords()
        );

        session.setTotalPossibleScore(session.getTotalPossibleScore() + eval.getMaxPoints());
        session.setScore(session.getScore() + eval.getEarnedPoints());

        if (!eval.isPassed()) {
            addTagMistakes(session, current);
            session.setFailReason(eval.getReason());

            session.setCurrentIndex(idx + 1);

            if (session.getCurrentIndex() >= session.getQuestions().size()) {
                finalizeSessionByThreshold(session);
                history.finalizeSession(sessionId, session.getStatus(), session.getFailReason());
                return new InterviewAnswerResponse(
                        session.getStatus(),
                        null,
                        session.getFailReason(),
                        session.getCurrentIndex(),
                        session.getQuestions().size(),
                        toDto(current),
                        session.getScore(),
                        session.getTotalPossibleScore(),
                        eval.getMissingKeywords(),
                        eval.getStrengths(),
                        eval.getFeedback(),
                        eval.getEvaluationSource(),
                        safeCopyMap(session.getTagMistakes())
                );
            }

            InterviewQuestion next = session.getQuestions().get(session.getCurrentIndex());
            return new InterviewAnswerResponse(
                    session.getStatus(),
                    toDto(next),
                    eval.getReason(),
                    session.getCurrentIndex(),
                    session.getQuestions().size(),
                    toDto(current),
                    session.getScore(),
                    session.getTotalPossibleScore(),
                    eval.getMissingKeywords(),
                    eval.getStrengths(),
                    eval.getFeedback(),
                    eval.getEvaluationSource(),
                    safeCopyMap(session.getTagMistakes())
            );
        }

        session.setCurrentIndex(idx + 1);

        if (session.getCurrentIndex() >= session.getQuestions().size()) {
            finalizeSessionByThreshold(session);
            history.finalizeSession(sessionId, session.getStatus(), session.getFailReason());
            return new InterviewAnswerResponse(
                    session.getStatus(),
                    null,
                    session.getFailReason(),
                    session.getCurrentIndex(),
                    session.getQuestions().size(),
                    toDto(current),
                    session.getScore(),
                    session.getTotalPossibleScore(),
                    List.of(),
                    eval.getStrengths(),
                    eval.getFeedback(),
                    eval.getEvaluationSource(),
                    safeCopyMap(session.getTagMistakes())
            );
        }

        InterviewQuestion next = session.getQuestions().get(session.getCurrentIndex());
        return new InterviewAnswerResponse(
                session.getStatus(),
                toDto(next),
                null,
                session.getCurrentIndex(),
                session.getQuestions().size(),
                toDto(current),
                session.getScore(),
                session.getTotalPossibleScore(),
                List.of(),
                eval.getStrengths(),
                eval.getFeedback(),
                eval.getEvaluationSource(),
                safeCopyMap(session.getTagMistakes())
        );
    }

    public InterviewSummaryResponse summary(String sessionId) {
        InterviewSession session = store.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }

        int totalQuestions = (session.getQuestions() == null) ? 0 : session.getQuestions().size();
        int answeredCount = (session.getAnswers() == null) ? 0 : session.getAnswers().size();

        int scoreSoFar = session.getScore();
        int maxScoreSoFar = session.getTotalPossibleScore();

        int percent = calcPercent(scoreSoFar, maxScoreSoFar);

        List<TagWeaknessDto> weakTags = buildWeakTags(session.getTagMistakes());
        List<TagBreakdownDto> breakdown = buildTagBreakdown(session);
        List<String> strengths = buildStrengths(breakdown);
        List<String> weaknesses = buildWeaknesses(breakdown);
        List<String> criticalFailures = buildCriticalFailures(session, percent);
        List<String> recs = buildRecommendations(weakTags, criticalFailures);

        boolean passed = percent >= PASS_THRESHOLD_PERCENT && criticalFailures.isEmpty();
        String passReason = buildPassReason(passed, percent, criticalFailures);
        DiagnosisDto diagnosis = buildDiagnosis(passed, percent, breakdown, criticalFailures);

        int overallScore = percent;

        return new InterviewSummaryResponse(
                session.getStatus(),
                totalQuestions,
                answeredCount,
                scoreSoFar,
                maxScoreSoFar,
                percent,
                toDto(session.getLastEvaluatedQuestion()),
                weakTags,
                recs,
                overallScore,
                passed,
                passReason,
                breakdown,
                strengths,
                weaknesses,
                criticalFailures,
                diagnosis
        );
    }

    private int calcPercent(int score, int maxScore) {
        if (maxScore <= 0) return 0;
        int p = (int) Math.round((score * 100.0) / maxScore);
        if (p < 0) p = 0;
        if (p > 100) p = 100;
        return p;
    }

    private void finalizeSessionByThreshold(InterviewSession session) {
        int percent = calcPercent(session.getScore(), session.getTotalPossibleScore());
        if (percent >= PASS_THRESHOLD_PERCENT) {
            session.setStatus(InterviewStatus.PASSED);
        } else {
            session.setStatus(InterviewStatus.FAILED);
            if (session.getFailReason() == null || session.getFailReason().isBlank()) {
                session.setFailReason("Score below " + PASS_THRESHOLD_PERCENT + "%");
            }
        }
    }

    private List<TagWeaknessDto> buildWeakTags(Map<String, Integer> tagMistakes) {
        if (tagMistakes == null || tagMistakes.isEmpty()) return new ArrayList<>();

        List<TagWeaknessDto> list = new ArrayList<>();
        for (Map.Entry<String, Integer> e : tagMistakes.entrySet()) {
            if (e.getKey() == null) continue;
            list.add(new TagWeaknessDto(e.getKey(), e.getValue() == null ? 0 : e.getValue()));
        }

        list.sort((a, b) -> Integer.compare(b.getMistakes(), a.getMistakes()));
        return list;
    }

    private List<String> buildRecommendations(List<TagWeaknessDto> weakTags, List<String> criticalFailures) {
        List<String> recs = new ArrayList<>();

        if (criticalFailures != null) {
            boolean hasEmpty = criticalFailures.stream()
                    .filter(Objects::nonNull)
                    .anyMatch(s -> s.toLowerCase().contains("empty"));
            if (hasEmpty) {
                recs.add("Do not leave answers empty. Write 2-3 short sentences even if unsure.");
            }
        }

        if (weakTags == null || weakTags.isEmpty()) return recs;

        int limit = Math.min(3, weakTags.size());
        for (int i = 0; i < limit; i++) {
            TagWeaknessDto t = weakTags.get(i);
            if (t == null || t.getTag() == null) continue;
            recs.add("Practice more: " + t.getTag());
        }
        return recs;
    }

    private void addTagMistakes(InterviewSession session, InterviewQuestion q) {
        if (q == null || q.getTags() == null) return;
        Map<String, Integer> map = session.getTagMistakes();
        if (map == null) {
            map = new HashMap<>();
            session.setTagMistakes(map);
        }
        for (String t : q.getTags()) {
            if (t == null) continue;
            map.put(t, map.getOrDefault(t, 0) + 1);
        }
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) return first;
        if (second != null && !second.isBlank()) return second;
        return null;
    }

    private Map<String, Integer> safeCopyMap(Map<String, Integer> src) {
        return (src == null) ? new HashMap<>() : new HashMap<>(src);
    }

    private String loadJobText(InterviewSourceType sourceType, String payload) {
        if (sourceType == InterviewSourceType.TEXT) {
            return payload;
        }

        ScraperResult res = JobScraperRouter.scrape(payload);
        String desc = res != null && res.getDescription() != null ? res.getDescription() : "";
        String req = (res != null && res.getRequirements() != null) ? String.join("\n", res.getRequirements()) : "";
        return (desc + "\n" + req).trim();
    }

    private String buildRoleHint(Set<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return "backend";
        }

        if (skills.contains("spring") || skills.contains("spring boot") || skills.contains("java")) {
            return "java-backend";
        }

        if (skills.contains("go")) {
            return "go-backend";
        }

        if (skills.contains("python")) {
            return "python-backend";
        }

        return "backend";
    }

    private QuestionDto toDto(InterviewQuestion q) {
        if (q == null) return null;
        return new QuestionDto(
                q.getId(),
                q.getText(),
                q.getType(),
                q.getOptions(),
                q.getStarterCode()
        );
    }

    public List<String> getAvailableTopics() {
        List<InterviewQuestion> bank = questionBank.getBackendJuniorBank();

        return bank.stream()
                .filter(q -> q.getTags() != null)
                .flatMap(q -> q.getTags().stream())
                .distinct()
                .sorted()
                .toList();
    }

    private List<TagBreakdownDto> buildTagBreakdown(InterviewSession session) {
        if (session == null || session.getQuestions() == null || session.getQuestions().isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Integer> tagCounts = new HashMap<>();
        for (InterviewQuestion q : session.getQuestions()) {
            if (q == null || q.getTags() == null) continue;
            for (String t : q.getTags()) {
                if (t == null || t.isBlank()) continue;
                tagCounts.put(t, tagCounts.getOrDefault(t, 0) + 1);
            }
        }

        Map<String, Integer> mistakes = session.getTagMistakes() == null ? Map.of() : session.getTagMistakes();

        List<TagBreakdownDto> out = new ArrayList<>();
        for (Map.Entry<String, Integer> e : tagCounts.entrySet()) {
            String tag = e.getKey();
            int count = e.getValue() == null ? 0 : e.getValue();
            int m = mistakes.getOrDefault(tag, 0);

            int avgScore = calcTagScoreFromMistakes(count, m);

            String severity = (avgScore >= STRONG_THRESHOLD) ? "STRONG"
                    : (avgScore >= WEAK_THRESHOLD) ? "OK"
                    : "WEAK";

            boolean passed = avgScore >= TAG_PASS_THRESHOLD;

            out.add(new TagBreakdownDto(tag, count, m, avgScore, severity, passed));
        }

        out.sort((a, b) -> {
            int c = Integer.compare(a.getAvgScore(), b.getAvgScore());
            if (c != 0) return c;
            return Integer.compare(b.getCount(), a.getCount());
        });

        return out;
    }

    private int calcTagScoreFromMistakes(int count, int mistakes) {
        if (count <= 0) return 100;
        if (mistakes < 0) mistakes = 0;
        if (mistakes > count) mistakes = count;

        double ratio = 1.0 - (mistakes / (double) count);
        int score = (int) Math.round(ratio * 100.0);

        if (score < 0) score = 0;
        if (score > 100) score = 100;
        return score;
    }

    private List<String> buildStrengths(List<TagBreakdownDto> breakdown) {
        if (breakdown == null || breakdown.isEmpty()) return List.of();

        return breakdown.stream()
                .filter(Objects::nonNull)
                .filter(t -> t.getCount() >= MIN_TAG_COUNT_FOR_STRENGTH_WEAKNESS)
                .filter(t -> t.getAvgScore() >= STRONG_THRESHOLD)
                .sorted((a, b) -> Integer.compare(b.getAvgScore(), a.getAvgScore()))
                .limit(3)
                .map(TagBreakdownDto::getTag)
                .toList();
    }

    private List<String> buildWeaknesses(List<TagBreakdownDto> breakdown) {
        if (breakdown == null || breakdown.isEmpty()) return List.of();

        return breakdown.stream()
                .filter(Objects::nonNull)
                .filter(t -> t.getCount() >= MIN_TAG_COUNT_FOR_STRENGTH_WEAKNESS)
                .filter(t -> t.getAvgScore() < WEAK_THRESHOLD)
                .sorted((a, b) -> Integer.compare(a.getAvgScore(), b.getAvgScore()))
                .limit(3)
                .map(TagBreakdownDto::getTag)
                .toList();
    }

    private List<String> buildCriticalFailures(InterviewSession session, int overallPercent) {
        List<String> out = new ArrayList<>();
        if (session == null) return out;

        int empty = 0;
        if (session.getAnswers() != null) {
            for (String a : session.getAnswers()) {
                if (a == null || a.isBlank()) empty++;
            }
        }

        if (empty >= 2) {
            out.add("Multiple empty answers (" + empty + "/10)");
        }

        if (overallPercent > 0 && overallPercent < CRIT_OVERALL_VERY_LOW) {
            out.add("Overall score extremely low (" + overallPercent + "%)");
        }

        return out;
    }

    private String buildPassReason(boolean passed, int percent, List<String> criticalFailures) {
        if (passed) {
            return "Overall score >= " + PASS_THRESHOLD_PERCENT + "% and no critical failures";
        }
        if (criticalFailures != null && !criticalFailures.isEmpty()) {
            return "Critical failures detected";
        }
        return "Overall score below " + PASS_THRESHOLD_PERCENT + "% (" + percent + "%)";
    }

    private DiagnosisDto buildDiagnosis(boolean passed,
                                        int overallPercent,
                                        List<TagBreakdownDto> breakdown,
                                        List<String> criticalFailures) {
        if (passed) return null;

        String primary = null;
        String secondary = null;

        if (breakdown != null) {
            for (TagBreakdownDto t : breakdown) {
                if (t == null) continue;
                if (t.getCount() < MIN_TAG_COUNT_FOR_STRENGTH_WEAKNESS) continue;

                if (primary == null) {
                    primary = t.getTag();
                    continue;
                }
                if (secondary == null && !t.getTag().equals(primary)) {
                    secondary = t.getTag();
                    break;
                }
            }
        }

        List<String> reasons = new ArrayList<>();
        if (criticalFailures != null && !criticalFailures.isEmpty()) {
            reasons.addAll(criticalFailures);
        }

        if (primary != null) reasons.add("Primary weakness by performance: " + primary);
        if (secondary != null) reasons.add("Secondary weakness by performance: " + secondary);

        double c = 0.55;
        if (overallPercent < 50) c += 0.15;
        if (overallPercent < 40) c += 0.10;
        if (criticalFailures != null && !criticalFailures.isEmpty()) c += 0.15;
        if (primary == null) c -= 0.20;

        if (c < 0.0) c = 0.0;
        if (c > 1.0) c = 1.0;

        return new DiagnosisDto(primary, secondary, c, reasons);
    }
}