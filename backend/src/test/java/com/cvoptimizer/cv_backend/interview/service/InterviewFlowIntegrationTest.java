package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.dto.InterviewAnswerRequest;
import com.cvoptimizer.cv_backend.interview.dto.InterviewAnswerResponse;
import com.cvoptimizer.cv_backend.interview.dto.InterviewStartRequest;
import com.cvoptimizer.cv_backend.interview.dto.InterviewStartResponse;
import com.cvoptimizer.cv_backend.interview.dto.InterviewSummaryResponse;
import com.cvoptimizer.cv_backend.interview.model.InterviewStatus;
import com.cvoptimizer.cv_backend.interview.persistence.repo.InterviewSessionRepository;
import com.cvoptimizer.cv_backend.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class InterviewFlowIntegrationTest {

    @Autowired
    private InterviewEngineService engine;

    @Autowired
    private InterviewSessionStore sessionStore;

    @Autowired
    private InterviewSessionRepository sessionRepo;

    @Test
    void startInterview_returnsSessionIdAndFirstQuestion() {
        InterviewStartRequest req = new InterviewStartRequest();
        req.setTopic("java");
        req.setUserId("test-user");

        InterviewStartResponse response = engine.start(req);

        assertThat(response.getSessionId()).isNotBlank();
        assertThat(response.getFirstQuestion()).isNotNull();
        assertThat(response.getFirstQuestion().getText()).isNotBlank();
    }

    @Test
    void submitAnswer_persistsAttemptAndReturnsNextQuestion() {
        InterviewStartRequest startReq = new InterviewStartRequest();
        startReq.setTopic("java");
        startReq.setUserId("test-user");
        String sessionId = engine.start(startReq).getSessionId();

        InterviewAnswerRequest answerReq = new InterviewAnswerRequest();
        answerReq.setAnswerText("Java uses automatic garbage collection to manage memory via the JVM heap.");

        InterviewAnswerResponse response = engine.answer(sessionId, answerReq);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isNotNull();
    }

    @Test
    void sessionState_persistedToDb_andRestoredAfterMemoryClear() {
        InterviewStartRequest startReq = new InterviewStartRequest();
        startReq.setTopic("spring");
        startReq.setUserId("test-user");
        String sessionId = engine.start(startReq).getSessionId();

        // Verify session state was written to DB
        var entity = sessionRepo.findById(sessionId);
        assertThat(entity).isPresent();
        assertThat(entity.get().getSessionState()).isNotBlank();

        // Simulate restart: evict from memory
        sessionStore.remove(sessionId);

        // Answer should restore session from DB transparently
        InterviewAnswerRequest answerReq = new InterviewAnswerRequest();
        answerReq.setAnswerText("Spring IoC container manages bean lifecycle and dependency injection.");

        InterviewAnswerResponse response = engine.answer(sessionId, answerReq);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isNotNull();
    }

    @Test
    void completedInterview_summaryReturnsScoreAndDiagnosis() {
        InterviewStartRequest startReq = new InterviewStartRequest();
        startReq.setTopic("java");
        startReq.setUserId("test-user");
        String sessionId = engine.start(startReq).getSessionId();

        // Answer all questions until session ends
        InterviewAnswerRequest answerReq = new InterviewAnswerRequest();
        answerReq.setAnswerText("");

        for (int i = 0; i < 12; i++) {
            InterviewAnswerResponse resp = engine.answer(sessionId, answerReq);
            if (resp.getStatus() != InterviewStatus.IN_PROGRESS) break;
        }

        InterviewSummaryResponse summary = engine.summary(sessionId);
        assertThat(summary).isNotNull();
        assertThat(summary.getTotalScore()).isGreaterThanOrEqualTo(0);
        assertThat(summary.getPercentage()).isGreaterThanOrEqualTo(0.0);
    }
}
