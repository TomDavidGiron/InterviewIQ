package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.agent.model.AgentAction;
import com.cvoptimizer.cv_backend.interview.agent.model.AgentDecision;
import com.cvoptimizer.cv_backend.interview.agent.service.InterviewAgentService;
import com.cvoptimizer.cv_backend.interview.dto.InterviewAnswerRequest;
import com.cvoptimizer.cv_backend.interview.dto.InterviewAnswerResponse;
import com.cvoptimizer.cv_backend.interview.dto.InterviewSummaryResponse;
import com.cvoptimizer.cv_backend.interview.feedback.dto.AiFeedbackResponse;
import com.cvoptimizer.cv_backend.interview.feedback.service.AiFeedbackService;
import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.interview.model.InterviewSession;
import com.cvoptimizer.cv_backend.interview.model.InterviewStatus;
import com.cvoptimizer.cv_backend.scraper.JobScraperRouter;
import com.cvoptimizer.cv_backend.service.JobSkillExtractorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InterviewEngineServiceTest {

    private InterviewEvaluatorService evaluator;
    private InterviewSessionStore store;
    private InterviewAgentService agent;
    private QuestionBankService questionBank;
    private AiFeedbackService aiFeedback;
    private InterviewEngineService engine;
    private InterviewSession session;

    @BeforeEach
    void setUp() {
        evaluator = mock(InterviewEvaluatorService.class);
        store = mock(InterviewSessionStore.class);
        agent = mock(InterviewAgentService.class);
        questionBank = mock(QuestionBankService.class);
        aiFeedback = mock(AiFeedbackService.class);

        engine = new InterviewEngineService(
                mock(JobSkillExtractorService.class),
                questionBank,
                mock(QuestionSelectorService.class),
                evaluator,
                store,
                mock(InterviewHistoryService.class),
                agent,
                mock(SkillGraphService.class),
                aiFeedback,
                mock(JobSpecificInterviewPlannerService.class),
                mock(JobScraperRouter.class)
        );

        session = new InterviewSession("s1", List.of(
                new InterviewQuestion("q1", "What is idempotency?", Set.of("rest"), false, Set.of("idempotent")),
                new InterviewQuestion("q2", "What is a JOIN?", Set.of("sql"), false, Set.of("join"))
        ), Set.of());
        when(store.get("s1")).thenReturn(session);
    }

    @Test
    void followUpAnswer_isEvaluatedAgainstTheFollowUpQuestion() {
        when(evaluator.evaluate(any(), anyString())).thenReturn(
                InterviewEvaluatorService.EvaluationResult.fail(
                        "Missing", List.of("idempotent"), 0, 1, List.of(), "Missing", "keyword"));
        when(agent.decide(any()))
                .thenReturn(AgentDecision.followUp("missed concept", "Follow-up: explain idempotent", false))
                .thenReturn(AgentDecision.nextQuestion("default"));

        InterviewAnswerResponse first = engine.answer("s1", answer("not sure"));
        String followUpId = first.getQuestion().getId();
        assertThat(followUpId).startsWith("followup-");

        InterviewAnswerResponse second = engine.answer("s1", answer("same result when repeated"));

        ArgumentCaptor<InterviewQuestion> evaluated = ArgumentCaptor.forClass(InterviewQuestion.class);
        verify(evaluator, times(2)).evaluate(evaluated.capture(), anyString());
        assertThat(evaluated.getAllValues().get(0).getId()).isEqualTo("q1");
        assertThat(evaluated.getAllValues().get(1).getId()).isEqualTo(followUpId);

        // After the follow-up, the interview moves on to the next planned question
        assertThat(second.getQuestion().getId()).isEqualTo("q2");
    }

    @Test
    void increaseDifficulty_withoutSameTopicHarderQuestion_keepsPlannedNextQuestion() {
        InterviewQuestion offTopicHard = new InterviewQuestion("css1", "Explain CSS containment", Set.of("css"), false, Set.of("contain"));
        offTopicHard.setDifficulty("HARD");
        when(questionBank.getAllFromDatabase()).thenReturn(List.of(offTopicHard));
        passAndIncreaseDifficulty();

        InterviewAnswerResponse response = engine.answer("s1", answer("same effect when repeated"));

        assertThat(response.getQuestion().getId()).isEqualTo("q2");
        assertThat(session.getCurrentDifficulty()).isEqualTo(3);
    }

    @Test
    void increaseDifficulty_withSameTopicHarderQuestion_servesIt() {
        InterviewQuestion restHard = new InterviewQuestion("rest-hard", "Design idempotent retries", Set.of("rest"), false, Set.of("retry"));
        restHard.setDifficulty("HARD");
        when(questionBank.getAllFromDatabase()).thenReturn(List.of(restHard));
        passAndIncreaseDifficulty();

        InterviewAnswerResponse response = engine.answer("s1", answer("same effect when repeated"));

        assertThat(response.getQuestion().getId()).isEqualTo("rest-hard");
    }

    @Test
    void finishedSession_aiSummaryIsGeneratedOnceAndReused() {
        session.setStatus(InterviewStatus.PASSED);
        when(aiFeedback.generateFeedback(any())).thenReturn(feedback("ai"));

        InterviewSummaryResponse first = engine.summary("s1");
        InterviewSummaryResponse second = engine.summary("s1");

        assertThat(first.getFeedbackSource()).isEqualTo("ai");
        assertThat(second).isSameAs(first);
        verify(aiFeedback, times(1)).generateFeedback(any());
    }

    @Test
    void ruleBasedFallback_isLabelledRuleAndNotCached() {
        session.setStatus(InterviewStatus.FAILED);
        when(aiFeedback.generateFeedback(any())).thenReturn(feedback("rule"));

        assertThat(engine.summary("s1").getFeedbackSource()).isEqualTo("rule");
        engine.summary("s1");

        verify(aiFeedback, times(2)).generateFeedback(any());
    }

    private AiFeedbackResponse feedback(String source) {
        AiFeedbackResponse response = new AiFeedbackResponse();
        response.setDiagnosis("Solid fundamentals");
        response.setStrengths(List.of());
        response.setWeaknesses(List.of());
        response.setStudyPlan("Review REST semantics");
        response.setFeedbackSummary("Good");
        response.setSource(source);
        return response;
    }

    private void passAndIncreaseDifficulty() {
        session.setCurrentDifficulty(2);
        when(evaluator.evaluate(any(), anyString())).thenReturn(
                InterviewEvaluatorService.EvaluationResult.pass(1, 1, List.of(), "Good", "keyword"));
        AgentDecision increase = new AgentDecision(AgentAction.INCREASE_DIFFICULTY, "streak");
        increase.setDifficultyDelta(1);
        when(agent.decide(any())).thenReturn(increase);
    }

    private InterviewAnswerRequest answer(String text) {
        InterviewAnswerRequest request = new InterviewAnswerRequest();
        request.setAnswerText(text);
        return request;
    }
}
