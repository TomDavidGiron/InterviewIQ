package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.agent.model.AgentAction;
import com.cvoptimizer.cv_backend.interview.agent.model.AgentContext;
import com.cvoptimizer.cv_backend.interview.agent.model.AgentDecision;
import com.cvoptimizer.cv_backend.interview.agent.service.InterviewAgentService;
import com.cvoptimizer.cv_backend.interview.dto.*;
import com.cvoptimizer.cv_backend.interview.dto.JobSpecificInterviewPlan;
import com.cvoptimizer.cv_backend.interview.feedback.dto.AiFeedbackRequest;
import com.cvoptimizer.cv_backend.interview.feedback.dto.AiFeedbackResponse;
import com.cvoptimizer.cv_backend.interview.feedback.dto.FeedbackAttemptDto;
import com.cvoptimizer.cv_backend.interview.feedback.service.AiFeedbackService;
import com.cvoptimizer.cv_backend.interview.model.*;
import com.cvoptimizer.cv_backend.model.ScraperResult;
import com.cvoptimizer.cv_backend.scraper.JobScraperRouter;
import com.cvoptimizer.cv_backend.scraper.SsrfGuard;
import com.cvoptimizer.cv_backend.service.JobSkillExtractorService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    private final InterviewAgentService interviewAgentService;
    private final SkillGraphService skillGraphService;
    private final AiFeedbackService aiFeedbackService;

    // Phase 6 additions
    private final JobSpecificInterviewPlannerService jobSpecificInterviewPlannerService;
    private final JobScraperRouter jobScraperRouter;

    public InterviewEngineService(
            JobSkillExtractorService skillExtractor,
            QuestionBankService questionBank,
            QuestionSelectorService selector,
            InterviewEvaluatorService evaluator,
            InterviewSessionStore store,
            InterviewHistoryService history,
            InterviewAgentService interviewAgentService,
            SkillGraphService skillGraphService,
            AiFeedbackService aiFeedbackService,
            JobSpecificInterviewPlannerService jobSpecificInterviewPlannerService,
            JobScraperRouter jobScraperRouter
    ) {
        this.skillExtractor = skillExtractor;
        this.questionBank = questionBank;
        this.selector = selector;
        this.evaluator = evaluator;
        this.store = store;
        this.history = history;
        this.interviewAgentService = interviewAgentService;
        this.skillGraphService = skillGraphService;
        this.aiFeedbackService = aiFeedbackService;
        this.jobSpecificInterviewPlannerService = jobSpecificInterviewPlannerService;
        this.jobScraperRouter = jobScraperRouter;
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
        List<InterviewQuestion> selected;
        List<String> prioritizedSkills = List.of();
        String resolvedJobText = "";

        if (payload.isBlank()) {
            skills = Collections.emptySet();

            List<InterviewQuestion> bank = questionBank.getAllFromDatabase();

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
            selected = selector.selectQuestions(skills, bank, n);

        } else {
            resolvedJobText = loadJobText(sourceType, payload);

            JobSpecificInterviewPlan plan =
                    jobSpecificInterviewPlannerService.buildPlan(resolvedJobText, request.getTopic());

            skills = plan.getExtractedSkills() == null
                    ? Collections.emptySet()
                    : new LinkedHashSet<>(plan.getExtractedSkills());

            prioritizedSkills = plan.getPrioritizedSkills() == null
                    ? List.of()
                    : plan.getPrioritizedSkills();

            selected = plan.getSelectedQuestions() == null
                    ? new ArrayList<>()
                    : plan.getSelectedQuestions();
        }

        if (selected == null || selected.isEmpty()) {
            List<InterviewQuestion> fallbackBank = questionBank.getAllFromDatabase();
            int n = Math.min(QUESTIONS_PER_SESSION, fallbackBank.size());
            selected = fallbackBank.stream()
                    .limit(n)
                    .collect(Collectors.toList());
        }

        String roleHint = buildRoleHint(skills);
        String levelHint = "junior";
        String sessionId = history.createSession(roleHint, levelHint, request.getUserId()).getId();

        InterviewSession session = new InterviewSession(sessionId, selected, skills);
        session.setUserId(request.getUserId() == null || request.getUserId().isBlank()
                ? "anonymous"
                : request.getUserId().trim());
        session.setCurrentDifficulty(2);
        session.setConsecutivePasses(0);
        session.setConsecutiveFails(0);
        session.setFollowUpCount(0);

        // Phase 6 session context
        session.setJobSpecific(jobSpecificOnly || !payload.isBlank());
        session.setJobDescriptionText(resolvedJobText);
        session.setPrioritizedJobSkills(prioritizedSkills);
        session.setTargetRoleHint(roleHint);

        store.put(session);

        InterviewQuestion first = selected.isEmpty() ? null : selected.get(0);
        return new InterviewStartResponse(sessionId, toDto(first));
    }

    private void finalizeAndPersistSession(String sessionId, InterviewSession session) {
        finalizeSessionByThreshold(session);
        history.finalizeSession(sessionId, session.getStatus(), session.getFailReason());
        skillGraphService.updateAfterInterview(session.getUserId(), buildTagBreakdown(session));
    }

    public InterviewAnswerResponse answer(String sessionId, InterviewAnswerRequest request) {
        InterviewSession session = store.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }

        if (session.getStatus() != InterviewStatus.IN_PROGRESS) {
            InterviewAnswerResponse response = new InterviewAnswerResponse(
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
            response.setAgentAction("NONE");
            response.setAgentReason("Session already finished");
            return response;
        }

        int idx = session.getCurrentIndex();

        if (idx < 0 || idx >= session.getQuestions().size()) {
            finalizeAndPersistSession(sessionId, session);

            InterviewAnswerResponse response = new InterviewAnswerResponse(
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
            response.setAgentAction("FINISH_INTERVIEW");
            response.setAgentReason("No remaining questions");
            return response;
        }

        InterviewQuestion current = session.getQuestions().get(idx);
        session.setLastEvaluatedQuestion(current);

        String answerText = (request != null) ? request.getAnswerText() : null;
        String safeAnswerText = answerText == null ? "" : answerText;
        session.getAnswers().add(safeAnswerText);

        InterviewEvaluatorService.EvaluationResult eval = evaluator.evaluate(current, answerText);

        history.appendAttempt(
                sessionId,
                current.getId(),
                current.getText(),
                safeAnswerText,
                firstNonBlank(eval.getFeedback(), eval.getReason()),
                eval.isPassed(),
                eval.getEarnedPoints(),
                eval.getMaxPoints(),
                eval.getMissingKeywords()
        );

        session.setTotalPossibleScore(session.getTotalPossibleScore() + eval.getMaxPoints());
        session.setScore(session.getScore() + eval.getEarnedPoints());

        updateStreaks(session, eval.isPassed());

        if (!eval.isPassed()) {
            addTagMistakes(session, current);
            session.setFailReason(eval.getReason());
        }

        AgentContext agentContext = buildAgentContext(session, current, safeAnswerText, eval);
        AgentDecision decision = interviewAgentService.decide(agentContext);

        if (decision == null || decision.getAction() == null) {
            decision = AgentDecision.nextQuestion("Agent returned null, fallback to default progression");
        }

        InterviewAnswerResponse response = executeAgentDecision(sessionId, session, current, eval, decision);
        session.setLastAgentAction(decision.getAction());
        response.setAgentAction(decision.getAction().name());
        response.setAgentReason(decision.getReason());
        store.put(session);
        return response;
    }

    public InterviewSummaryResponse summary(String sessionId) {
        InterviewSession session = store.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }

        int scoreSoFar = session.getScore();
        int maxScoreSoFar = session.getTotalPossibleScore();
        int percent = calcPercent(scoreSoFar, maxScoreSoFar);

        List<TagWeaknessDto> weakTags = buildWeakTags(session.getTagMistakes());
        List<TagBreakdownDto> breakdown = buildTagBreakdown(session);
        List<String> strengths = buildStrengths(breakdown);
        List<String> weaknesses = buildWeaknesses(breakdown);
        List<String> criticalFailures = buildCriticalFailures(session, percent);

        List<InterviewAttemptDto> attempts = history.getSessionDetails(sessionId);
        List<FeedbackAttemptDto> feedbackAttempts = toFeedbackAttempts(attempts);

        strengths = mergeSummaryStrengths(strengths, feedbackAttempts);
        weaknesses = mergeSummaryWeaknesses(weaknesses, weakTags, feedbackAttempts);

        List<String> recs = buildRecommendations(weakTags, criticalFailures);
        if (session.isJobSpecific()
                && session.getPrioritizedJobSkills() != null
                && !session.getPrioritizedJobSkills().isEmpty()) {
            int limit = Math.min(3, session.getPrioritizedJobSkills().size());
            for (int i = 0; i < limit; i++) {
                recs.add("Review job-relevant skill: " + session.getPrioritizedJobSkills().get(i));
            }
        }

        boolean passed = percent >= PASS_THRESHOLD_PERCENT && criticalFailures.isEmpty();
        DiagnosisDto diagnosis = buildDiagnosis(passed, percent, breakdown, criticalFailures);

        InterviewSummaryResponse summary = new InterviewSummaryResponse();
        summary.setSessionId(sessionId);
        summary.setTotalScore(scoreSoFar);
        summary.setMaxScore(maxScoreSoFar);
        summary.setPercentage(percent);
        summary.setOverallScore(String.valueOf(percent));
        summary.setStrengths(strengths);
        summary.setWeaknesses(weaknesses);

        List<String> weakTopicNames = weakTags.stream()
                .map(TagWeaknessDto::getTag)
                .filter(Objects::nonNull)
                .toList();

        summary.setWeakTopics(weakTopicNames);
        summary.setTagBreakdown(breakdown);
        summary.setTagWeaknesses(weakTags);
        summary.setDiagnosis(diagnosis);
        summary.setTargetRoleHint(session.getTargetRoleHint());

        try {
            AiFeedbackRequest feedbackRequest = new AiFeedbackRequest();
            feedbackRequest.setOverallScore(percent);
            feedbackRequest.setStrengths(strengths);
            feedbackRequest.setWeaknesses(weaknesses);
            feedbackRequest.setWeakTopics(weakTopicNames);
            feedbackRequest.setCriticalFailures(criticalFailures);
            feedbackRequest.setTagBreakdown(toTagBreakdownMaps(breakdown));
            feedbackRequest.setSkillGraph(toSkillGraphMap(breakdown));
            feedbackRequest.setAttempts(feedbackAttempts);
            feedbackRequest.setRoleHint(session.getTargetRoleHint());

            AiFeedbackResponse aiFeedback = aiFeedbackService.generateFeedback(feedbackRequest);

            DiagnosisDto aiDiagnosis = new DiagnosisDto();
            aiDiagnosis.setOverallAssessment(firstNonBlank(aiFeedback.getDiagnosis(), diagnosis.getOverallAssessment()));
            aiDiagnosis.setOverallScore(percent);
            aiDiagnosis.setPrimaryWeakAreas(mergeOrderedLists(summary.getWeaknesses(), aiFeedback.getWeaknesses()));
            aiDiagnosis.setCriticalIssues(criticalFailures);
            aiDiagnosis.setSuggestedStudyPlan(toStudyPlanLines(firstNonBlank(aiFeedback.getStudyPlan(), String.join("\n", diagnosis.getSuggestedStudyPlan()))));

            summary.setDiagnosis(aiDiagnosis);
            // Don't show AI-invented strengths when overall performance is very poor
            List<String> aiStrengths = (percent < 30) ? List.of() : aiFeedback.getStrengths();
            summary.setStrengths(mergeOrderedLists(summary.getStrengths(), aiStrengths));
            summary.setWeaknesses(mergeOrderedLists(summary.getWeaknesses(), aiFeedback.getWeaknesses()));
            summary.setStudyPlan(aiFeedback.getStudyPlan());
            summary.setFeedbackSummary(aiFeedback.getFeedbackSummary());
            summary.setFeedbackSource("ai");
        } catch (Exception e) {
            summary.setStudyPlan(String.join("\n", recs));
            summary.setFeedbackSummary(diagnosis.getOverallAssessment());
            summary.setFeedbackSource("rule");
        }

        return summary;
    }

    private AgentContext buildAgentContext(InterviewSession session,
                                           InterviewQuestion current,
                                           String answerText,
                                           InterviewEvaluatorService.EvaluationResult eval) {
        AgentContext context = new AgentContext();

        context.setSessionId(session.getSessionId());
        context.setCurrentQuestion(current);
        context.setUserAnswer(answerText);

        context.setQuestionIndex(session.getCurrentIndex());
        context.setTotalQuestions(session.getQuestions() == null ? 0 : session.getQuestions().size());

        context.setScoreSoFar(session.getScore());
        context.setMaxScoreSoFar(session.getTotalPossibleScore());
        context.setCurrentPercent(calcPercent(session.getScore(), session.getTotalPossibleScore()));

        context.setLastEvaluationPassed(eval.isPassed());
        context.setMissingConcepts(eval.getMissingKeywords() == null ? List.of() : eval.getMissingKeywords());
        context.setStrengths(eval.getStrengths() == null ? List.of() : eval.getStrengths());
        context.setFeedback(firstNonBlank(eval.getFeedback(), eval.getReason()));

        context.setExtractedSkills(session.getExtractedSkills() == null
                ? Set.of()
                : new HashSet<>(session.getExtractedSkills()));

        context.setWeakTopics(session.getTagMistakes() == null
                ? Set.of()
                : new HashSet<>(session.getTagMistakes().keySet()));

        context.setHistory(List.of());
        context.setJobSpecificMode(session.getExtractedSkills() != null && !session.getExtractedSkills().isEmpty());

        context.setCurrentDifficulty(session.getCurrentDifficulty());
        context.setConsecutivePasses(session.getConsecutivePasses());
        context.setConsecutiveFails(session.getConsecutiveFails());
        context.setFollowUpCount(session.getFollowUpCount());
        context.setLastAgentAction(session.getLastAgentAction());

        return context;
    }

    private InterviewAnswerResponse executeAgentDecision(String sessionId,
                                                         InterviewSession session,
                                                         InterviewQuestion current,
                                                         InterviewEvaluatorService.EvaluationResult eval,
                                                         AgentDecision decision) {

        AgentAction action = decision.getAction();

        if (action == AgentAction.FINISH_INTERVIEW) {
            session.setCurrentIndex(session.getQuestions().size());
            finalizeAndPersistSession(sessionId, session);

            return new InterviewAnswerResponse(
                    session.getStatus(),
                    null,
                    firstNonBlank(session.getFailReason(), decision.getReason()),
                    session.getCurrentIndex(),
                    session.getQuestions().size(),
                    toDto(current),
                    session.getScore(),
                    session.getTotalPossibleScore(),
                    safeList(eval.getMissingKeywords()),
                    safeList(eval.getStrengths()),
                    eval.getFeedback(),
                    eval.getEvaluationSource(),
                    safeCopyMap(session.getTagMistakes())
            );
        }

        if (action == AgentAction.ASK_FOLLOW_UP) {
            InterviewQuestion followUp = buildFollowUpQuestion(current, decision, eval);

            int insertIndex = session.getCurrentIndex() + 1;
            session.getQuestions().add(insertIndex, followUp);
            // Do NOT advance currentIndex here — the normal increment at line ~576 will move into the follow-up next call
            session.setFollowUpCount(session.getFollowUpCount() + 1);

            return new InterviewAnswerResponse(
                    session.getStatus(),
                    toDto(followUp),
                    eval.getReason(),
                    session.getCurrentIndex(),
                    session.getQuestions().size(),
                    toDto(current),
                    session.getScore(),
                    session.getTotalPossibleScore(),
                    safeList(eval.getMissingKeywords()),
                    safeList(eval.getStrengths()),
                    eval.getFeedback(),
                    eval.getEvaluationSource(),
                    safeCopyMap(session.getTagMistakes())
            );
        }

        if (action == AgentAction.SWITCH_TOPIC) {
            InterviewQuestion switched = findQuestionForTopicAndDifficulty(
                    session,
                    decision.getNextTopic(),
                    session.getCurrentDifficulty()
            );

            if (switched != null) {
                int nextIndex = session.getCurrentIndex() + 1;

                if (nextIndex < session.getQuestions().size()) {
                    session.getQuestions().set(nextIndex, switched);
                } else {
                    session.getQuestions().add(switched);
                }

                session.setCurrentIndex(nextIndex);

                return new InterviewAnswerResponse(
                        session.getStatus(),
                        toDto(switched),
                        eval.getReason(),
                        session.getCurrentIndex(),
                        session.getQuestions().size(),
                        toDto(current),
                        session.getScore(),
                        session.getTotalPossibleScore(),
                        safeList(eval.getMissingKeywords()),
                        safeList(eval.getStrengths()),
                        eval.getFeedback(),
                        eval.getEvaluationSource(),
                        safeCopyMap(session.getTagMistakes())
                );
            }
        }

        if (action == AgentAction.INCREASE_DIFFICULTY) {
            session.setCurrentDifficulty(session.getCurrentDifficulty() + 1);

            InterviewQuestion nextHarder = findQuestionForDifficulty(
                    session,
                    session.getCurrentDifficulty(),
                    current == null ? null : current.getTags()
            );

            if (nextHarder != null) {
                int nextIndex = session.getCurrentIndex() + 1;

                if (nextIndex < session.getQuestions().size()) {
                    session.getQuestions().set(nextIndex, nextHarder);
                } else {
                    session.getQuestions().add(nextHarder);
                }

                session.setCurrentIndex(nextIndex);

                return new InterviewAnswerResponse(
                        session.getStatus(),
                        toDto(nextHarder),
                        eval.isPassed() ? null : eval.getReason(),
                        session.getCurrentIndex(),
                        session.getQuestions().size(),
                        toDto(current),
                        session.getScore(),
                        session.getTotalPossibleScore(),
                        safeList(eval.getMissingKeywords()),
                        safeList(eval.getStrengths()),
                        eval.getFeedback(),
                        eval.getEvaluationSource(),
                        safeCopyMap(session.getTagMistakes())
                );
            }
        }

        if (action == AgentAction.DECREASE_DIFFICULTY) {
            session.setCurrentDifficulty(session.getCurrentDifficulty() - 1);

            InterviewQuestion nextEasier = findQuestionForDifficulty(
                    session,
                    session.getCurrentDifficulty(),
                    current == null ? null : current.getTags()
            );

            if (nextEasier != null) {
                int nextIndex = session.getCurrentIndex() + 1;

                if (nextIndex < session.getQuestions().size()) {
                    session.getQuestions().set(nextIndex, nextEasier);
                } else {
                    session.getQuestions().add(nextEasier);
                }

                session.setCurrentIndex(nextIndex);

                return new InterviewAnswerResponse(
                        session.getStatus(),
                        toDto(nextEasier),
                        eval.isPassed() ? null : eval.getReason(),
                        session.getCurrentIndex(),
                        session.getQuestions().size(),
                        toDto(current),
                        session.getScore(),
                        session.getTotalPossibleScore(),
                        safeList(eval.getMissingKeywords()),
                        safeList(eval.getStrengths()),
                        eval.getFeedback(),
                        eval.getEvaluationSource(),
                        safeCopyMap(session.getTagMistakes())
                );
            }
        }

        session.setCurrentIndex(session.getCurrentIndex() + 1);

        if (session.getCurrentIndex() >= session.getQuestions().size()) {
            finalizeAndPersistSession(sessionId, session);

            return new InterviewAnswerResponse(
                    session.getStatus(),
                    null,
                    session.getFailReason(),
                    session.getCurrentIndex(),
                    session.getQuestions().size(),
                    toDto(current),
                    session.getScore(),
                    session.getTotalPossibleScore(),
                    safeList(eval.getMissingKeywords()),
                    safeList(eval.getStrengths()),
                    eval.getFeedback(),
                    eval.getEvaluationSource(),
                    safeCopyMap(session.getTagMistakes())
            );
        }

        InterviewQuestion next = session.getQuestions().get(session.getCurrentIndex());

        return new InterviewAnswerResponse(
                session.getStatus(),
                toDto(next),
                eval.isPassed() ? null : eval.getReason(),
                session.getCurrentIndex(),
                session.getQuestions().size(),
                toDto(current),
                session.getScore(),
                session.getTotalPossibleScore(),
                safeList(eval.getMissingKeywords()),
                safeList(eval.getStrengths()),
                eval.getFeedback(),
                eval.getEvaluationSource(),
                safeCopyMap(session.getTagMistakes())
        );
    }

    private InterviewQuestion buildFollowUpQuestion(InterviewQuestion current,
                                                    AgentDecision decision,
                                                    InterviewEvaluatorService.EvaluationResult eval) {
        String followUpText = firstNonBlank(
                decision.getFollowUpPrompt(),
                buildFallbackFollowUpText(eval)
        );

        Set<String> tags = current != null && current.getTags() != null
                ? new HashSet<>(current.getTags())
                : Set.of("follow-up");

        Set<String> requiredKeywords = eval.getMissingKeywords() == null || eval.getMissingKeywords().isEmpty()
                ? Set.of()
                : new HashSet<>(eval.getMissingKeywords());

        return new InterviewQuestion(
                "followup-" + UUID.randomUUID(),
                followUpText,
                tags,
                false,
                requiredKeywords
        );
    }

    private String buildFallbackFollowUpText(InterviewEvaluatorService.EvaluationResult eval) {
        List<String> missing = safeList(eval.getMissingKeywords());
        if (!missing.isEmpty()) {
            return "Follow-up: explain these missing concepts with a practical example: " + String.join(", ", missing);
        }
        return "Follow-up: please go one level deeper and explain your reasoning with a practical real-world example.";
    }

    private InterviewQuestion findQuestionForTopicAndDifficulty(InterviewSession session, String topic, int difficulty) {
        if (topic == null || topic.isBlank()) {
            return null;
        }

        List<InterviewQuestion> bank = questionBank.getAllFromDatabase();
        Set<String> alreadyAskedIds = collectAskedIds(session);

        List<InterviewQuestion> exact = bank.stream()
                .filter(Objects::nonNull)
                .filter(q -> q.getTags() != null && q.getTags().contains(topic))
                .filter(q -> q.getId() != null && !alreadyAskedIds.contains(q.getId()))
                .filter(q -> estimateDifficulty(q) == difficulty)
                .toList();

        if (!exact.isEmpty()) {
            return exact.get(0);
        }

        return bank.stream()
                .filter(Objects::nonNull)
                .filter(q -> q.getTags() != null && q.getTags().contains(topic))
                .filter(q -> q.getId() != null && !alreadyAskedIds.contains(q.getId()))
                .findFirst()
                .orElse(null);
    }

    private InterviewQuestion findQuestionForDifficulty(InterviewSession session,
                                                        int difficulty,
                                                        Set<String> preferredTags) {
        List<InterviewQuestion> bank = questionBank.getAllFromDatabase();
        Set<String> alreadyAskedIds = collectAskedIds(session);

        List<InterviewQuestion> matchingPreferred = bank.stream()
                .filter(Objects::nonNull)
                .filter(q -> q.getId() != null && !alreadyAskedIds.contains(q.getId()))
                .filter(q -> estimateDifficulty(q) == difficulty)
                .filter(q -> hasAnyPreferredTag(q, preferredTags))
                .toList();

        if (!matchingPreferred.isEmpty()) {
            return matchingPreferred.get(0);
        }

        return bank.stream()
                .filter(Objects::nonNull)
                .filter(q -> q.getId() != null && !alreadyAskedIds.contains(q.getId()))
                .filter(q -> estimateDifficulty(q) == difficulty)
                .findFirst()
                .orElse(null);
    }

    private Set<String> collectAskedIds(InterviewSession session) {
        Set<String> alreadyAskedIds = new HashSet<>();

        if (session.getQuestions() != null) {
            for (InterviewQuestion q : session.getQuestions()) {
                if (q != null && q.getId() != null) {
                    alreadyAskedIds.add(q.getId());
                }
            }
        }

        return alreadyAskedIds;
    }

    private boolean hasAnyPreferredTag(InterviewQuestion q, Set<String> preferredTags) {
        if (q == null || q.getTags() == null || q.getTags().isEmpty()) {
            return false;
        }
        if (preferredTags == null || preferredTags.isEmpty()) {
            return false;
        }
        for (String tag : preferredTags) {
            if (q.getTags().contains(tag)) {
                return true;
            }
        }
        return false;
    }

    private int estimateDifficulty(InterviewQuestion q) {
        if (q == null) return 2;
        if (q.getDifficulty() != null) {
            return switch (q.getDifficulty().toUpperCase()) {
                case "EASY"  -> 1;
                case "HARD"  -> 3;
                default      -> 2;
            };
        }
        // legacy heuristic fallback for questions without a difficulty tag
        int score = 0;
        if (q.getType() == QuestionType.CODE) {
            score += 2;
        } else if (q.getType() == QuestionType.MCQ) {
            score += 1;
        }
        if (q.getRequiredKeywords() != null) {
            int kw = q.getRequiredKeywords().size();
            if (kw >= 5) score += 2;
            else if (kw >= 3) score += 1;
        }
        String text = q.getText() == null ? "" : q.getText().toLowerCase();
        if (text.contains("design") || text.contains("tradeoff") || text.contains("scal") ||
                text.contains("concurrency") || text.contains("distributed") || text.contains("optimiz")) {
            score += 2;
        }
        if (text.contains("difference between") || text.contains("compare") || text.contains("why")) {
            score += 1;
        }

        if (score <= 1) return 1;
        if (score <= 3) return 2;
        return 3;
    }

    private void updateStreaks(InterviewSession session, boolean passed) {
        if (passed) {
            session.setConsecutivePasses(session.getConsecutivePasses() + 1);
            session.setConsecutiveFails(0);
        } else {
            session.setConsecutiveFails(session.getConsecutiveFails() + 1);
            session.setConsecutivePasses(0);
        }
    }

    private List<String> safeList(List<String> src) {
        return src == null ? List.of() : src;
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
            if (t == null || t.isBlank()) continue;
            String normalized = t.trim().toLowerCase();
            map.put(normalized, map.getOrDefault(normalized, 0) + 1);
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

        SsrfGuard.assertSafe(payload);
        ScraperResult res = jobScraperRouter.scrape(payload);

        if (res == null) {
            return "";
        }

        String desc = res.getDescription() != null ? res.getDescription() : "";
        String req = "";

        if (res.getRequirements() != null) {
            if (res.getRequirements() instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<String> requirements = (List<String>) res.getRequirements();
                req = String.join("\n", requirements);
            } else {
                req = String.valueOf(res.getRequirements());
            }
        }

        String title = res.getTitle() != null ? res.getTitle() : "";
        String company = res.getCompany() != null ? res.getCompany() : "";

        return (title + "\n" + company + "\n" + desc + "\n" + req).trim();
    }

    private String buildRoleHint(Set<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return "backend";
        }

        Set<String> lower = skills.stream()
                .filter(s -> s != null)
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toSet());

        if (lower.contains("spring") || lower.contains("spring boot") || lower.contains("java")) {
            return "java-backend";
        }

        if (lower.contains("go")) {
            return "go-backend";
        }

        if (lower.contains("python")) {
            return "python-backend";
        }

        if (lower.contains("etl") || lower.contains("airflow") || lower.contains("dbt")) {
            return "data-engineer";
        }

        if (lower.contains("data analytics")) {
            return "data-analyst";
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
        List<InterviewQuestion> bank = questionBank.getAllFromDatabase();

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

        int servedCount = session.getAnswers() != null ? session.getAnswers().size() : session.getQuestions().size();
        List<InterviewQuestion> servedQuestions = session.getQuestions().subList(0, Math.min(servedCount, session.getQuestions().size()));

        Map<String, Integer> tagCounts = new HashMap<>();
        for (InterviewQuestion q : servedQuestions) {
            if (q == null || q.getTags() == null) continue;
            for (String t : q.getTags()) {
                if (t == null || t.isBlank()) continue;
                String normalized = t.trim().toLowerCase();
                tagCounts.put(normalized, tagCounts.getOrDefault(normalized, 0) + 1);
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

        int emptyAnswers = 0;
        if (session.getAnswers() != null) {
            for (String a : session.getAnswers()) {
                if (a == null || a.isBlank()) emptyAnswers++;
            }
        }
        if (emptyAnswers >= 2) {
            out.add("Multiple empty answers (" + emptyAnswers + ")");
        }

        if (overallPercent < CRIT_OVERALL_VERY_LOW) {
            out.add("Overall score below " + CRIT_OVERALL_VERY_LOW + "%");
        }

        return out;
    }

    private String buildPassReason(boolean passed, int percent, List<String> criticalFailures) {
        if (passed) {
            return "Passed with " + percent + "% and no critical failures";
        }

        if (criticalFailures != null && !criticalFailures.isEmpty()) {
            return "Did not pass due to critical failures: " + String.join("; ", criticalFailures);
        }

        return "Did not reach passing threshold of " + PASS_THRESHOLD_PERCENT + "%";
    }

    private List<FeedbackAttemptDto> toFeedbackAttempts(List<InterviewAttemptDto> attempts) {
        if (attempts == null || attempts.isEmpty()) {
            return List.of();
        }

        List<FeedbackAttemptDto> out = new ArrayList<>();
        for (InterviewAttemptDto attempt : attempts) {
            if (attempt == null) {
                continue;
            }

            FeedbackAttemptDto dto = new FeedbackAttemptDto();
            dto.setQuestion(attempt.getQuestionText());
            dto.setUserAnswer(attempt.getAnswerText());
            dto.setScore(calcPercent(attempt.getEarnedPoints(), attempt.getMaxPoints()));
            dto.setMissingConcepts(attempt.getMissingKeywords() == null ? List.of() : attempt.getMissingKeywords());
            out.add(dto);
        }
        return out;
    }

    private List<String> mergeSummaryStrengths(List<String> ruleStrengths, List<FeedbackAttemptDto> attempts) {
        LinkedHashSet<String> merged = new LinkedHashSet<>();
        if (ruleStrengths != null) {
            merged.addAll(ruleStrengths);
        }

        if (attempts != null) {
            int totalScore = attempts.stream().mapToInt(a -> a == null ? 0 : a.getScore()).sum();
            int avgScore = attempts.isEmpty() ? 0 : totalScore / attempts.size();

            // Only surface per-question strengths when overall performance is decent
            if (avgScore >= 50) {
                for (FeedbackAttemptDto attempt : attempts) {
                    if (attempt == null) continue;
                    if (attempt.getScore() >= 75 && attempt.getQuestion() != null && !attempt.getQuestion().isBlank()) {
                        merged.add(shortenLabel(attempt.getQuestion()));
                    }
                    if (merged.size() >= 3) break;
                }
            }
        }

        return merged.stream().limit(3).toList();
    }

    private List<String> mergeSummaryWeaknesses(List<String> ruleWeaknesses,
                                                List<TagWeaknessDto> weakTags,
                                                List<FeedbackAttemptDto> attempts) {
        LinkedHashSet<String> merged = new LinkedHashSet<>();
        if (ruleWeaknesses != null) {
            merged.addAll(ruleWeaknesses);
        }
        if (weakTags != null) {
            for (TagWeaknessDto tag : weakTags) {
                if (tag != null && tag.getTag() != null && !tag.getTag().isBlank()) {
                    merged.add(tag.getTag());
                }
                if (merged.size() >= 3) {
                    break;
                }
            }
        }
        if (attempts != null) {
            for (FeedbackAttemptDto attempt : attempts) {
                if (attempt == null || attempt.getMissingConcepts() == null) {
                    continue;
                }
                for (String missing : attempt.getMissingConcepts()) {
                    if (missing != null && !missing.isBlank()) {
                        merged.add(missing.trim());
                    }
                    if (merged.size() >= 3) {
                        break;
                    }
                }
                if (merged.size() >= 3) {
                    break;
                }
            }
        }
        return merged.stream().limit(3).toList();
    }

    private List<Map<String, Object>> toTagBreakdownMaps(List<TagBreakdownDto> breakdown) {
        if (breakdown == null || breakdown.isEmpty()) {
            return List.of();
        }

        List<Map<String, Object>> out = new ArrayList<>();
        for (TagBreakdownDto tag : breakdown) {
            if (tag == null) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("tag", tag.getTag());
            row.put("count", tag.getCount());
            row.put("mistakes", tag.getMistakes());
            row.put("avgScore", tag.getAvgScore());
            row.put("severity", tag.getSeverity());
            row.put("passed", tag.isPassed());
            out.add(row);
        }
        return out;
    }

    private Map<String, Integer> toSkillGraphMap(List<TagBreakdownDto> breakdown) {
        if (breakdown == null || breakdown.isEmpty()) {
            return Map.of();
        }

        Map<String, Integer> out = new LinkedHashMap<>();
        for (TagBreakdownDto tag : breakdown) {
            if (tag != null && tag.getTag() != null && !tag.getTag().isBlank()) {
                out.put(tag.getTag(), tag.getAvgScore());
            }
        }
        return out;
    }

    private List<String> mergeOrderedLists(List<String> primary, List<String> secondary) {
        LinkedHashSet<String> merged = new LinkedHashSet<>();
        if (primary != null) {
            for (String value : primary) {
                if (value != null && !value.isBlank()) {
                    merged.add(value.trim());
                }
            }
        }
        if (secondary != null) {
            for (String value : secondary) {
                if (value != null && !value.isBlank()) {
                    merged.add(value.trim());
                }
            }
        }
        return merged.stream().limit(3).toList();
    }

    private List<String> toStudyPlanLines(String studyPlan) {
        if (studyPlan == null || studyPlan.isBlank()) {
            return List.of();
        }
        return Arrays.stream(studyPlan.split("\r?\n"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private String shortenLabel(String text) {
        if (text == null) {
            return "";
        }
        String cleaned = text.replaceAll("\s+", " ").trim();
        if (cleaned.length() <= 42) {
            return cleaned;
        }
        return cleaned.substring(0, 42).trim() + "...";
    }

    private DiagnosisDto buildDiagnosis(boolean passed,
                                        int overallPercent,
                                        List<TagBreakdownDto> breakdown,
                                        List<String> criticalFailures) {
        DiagnosisDto d = new DiagnosisDto();
        d.setOverallAssessment(
                passed
                        ? "Solid performance with good readiness for the target interview"
                        : "Needs improvement before being interview-ready"
        );
        d.setOverallScore(overallPercent);

        List<String> primaryWeakAreas = new ArrayList<>();
        if (breakdown != null) {
            breakdown.stream()
                    .filter(Objects::nonNull)
                    .filter(t -> t.getCount() >= MIN_TAG_COUNT_FOR_STRENGTH_WEAKNESS)
                    .filter(t -> t.getAvgScore() < WEAK_THRESHOLD)
                    .sorted((a, b) -> Integer.compare(a.getAvgScore(), b.getAvgScore()))
                    .limit(2)
                    .map(TagBreakdownDto::getTag)
                    .forEach(primaryWeakAreas::add);
        }
        d.setPrimaryWeakAreas(primaryWeakAreas);

        d.setCriticalIssues(criticalFailures == null ? List.of() : criticalFailures);

        List<String> studyPlan = new ArrayList<>();
        for (String weak : primaryWeakAreas) {
            studyPlan.add("Review and practice " + weak + " fundamentals with 5-10 interview questions");
        }
        if (!passed && studyPlan.isEmpty()) {
            studyPlan.add("Practice answering aloud and structure answers with short concrete examples");
        }
        d.setSuggestedStudyPlan(studyPlan);

        return d;
    }
}