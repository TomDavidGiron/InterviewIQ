package com.cvoptimizer.cv_backend.interview.feedback;

import com.cvoptimizer.cv_backend.interview.ai.config.AiEvaluationProperties;
import com.cvoptimizer.cv_backend.interview.feedback.dto.AiFeedbackRequest;
import com.cvoptimizer.cv_backend.interview.feedback.dto.AiFeedbackResponse;
import com.cvoptimizer.cv_backend.interview.feedback.dto.FeedbackAttemptDto;
import com.cvoptimizer.cv_backend.interview.feedback.service.AiFeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiFeedbackServiceTest {

    @Mock private AiEvaluationProperties properties;
    @Mock private RestTemplate restTemplate;

    private AiFeedbackService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = new AiFeedbackService(properties, objectMapper, restTemplate);
    }

    @Test
    void generateFeedback_nullRequest_returnsFallback() {
        // null request short-circuits before isConfigured() is called — no stub needed
        AiFeedbackResponse result = service.generateFeedback(null);
        assertThat(result).isNotNull();
        assertThat(result.getDiagnosis()).isNotBlank();
    }

    @Test
    void generateFeedback_notConfigured_returnsFallback() {
        when(properties.isConfigured()).thenReturn(false);
        AiFeedbackResponse result = service.generateFeedback(request(75, List.of("caching"), List.of("transactions")));
        assertThat(result.getDiagnosis()).isNotBlank();
        assertThat(result.getStudyPlan()).isNotBlank();
    }

    @Test
    void generateFeedback_validAiResponse_parsedCorrectly() throws Exception {
        configureProperties();
        AiFeedbackResponse aiResp = new AiFeedbackResponse();
        aiResp.setDiagnosis("Strong candidate.");
        aiResp.setStrengths(List.of("OOP", "concurrency"));
        aiResp.setWeaknesses(List.of("distributed systems"));
        aiResp.setStudyPlan("1. Study Kafka\n2. Practice CAP theorem\n3. Re-test");
        aiResp.setFeedbackSummary("Good session, review distributed topics.");
        String json = objectMapper.writeValueAsString(aiResp);
        stubOpenAiResponse("{\"output_text\": " + objectMapper.valueToTree(json) + "}");

        AiFeedbackResponse result = service.generateFeedback(request(80, List.of("OOP"), List.of()));

        assertThat(result.getDiagnosis()).isEqualTo("Strong candidate.");
        assertThat(result.getStrengths()).containsExactly("OOP", "concurrency");
        assertThat(result.getWeaknesses()).containsExactly("distributed systems");
        assertThat(result.getStudyPlan()).contains("Kafka");
    }

    @Test
    void generateFeedback_apiThrows_returnsFallback() {
        configureProperties();
        when(restTemplate.exchange(any(String.class), any(), any(), eq(String.class)))
                .thenThrow(new RestClientException("connection refused"));

        AiFeedbackResponse result = service.generateFeedback(request(60, List.of(), List.of("locking")));
        assertThat(result).isNotNull();
        assertThat(result.getDiagnosis()).isNotBlank();
    }

    @Test
    void generateFeedback_highScore_fallbackDiagnosisIsPositive() {
        when(properties.isConfigured()).thenReturn(false);
        AiFeedbackResponse result = service.generateFeedback(request(90, List.of("JVM"), List.of()));
        assertThat(result.getDiagnosis()).containsIgnoringCase("strong");
    }

    @Test
    void generateFeedback_lowScore_fallbackDiagnosisRecommendsPractice() {
        when(properties.isConfigured()).thenReturn(false);
        AiFeedbackResponse result = service.generateFeedback(request(30, List.of(), List.of("transactions", "locks")));
        assertThat(result.getDiagnosis()).containsIgnoringCase("practice");
    }

    @Test
    void generateFeedback_weaknessesInferredFromAttempts() {
        when(properties.isConfigured()).thenReturn(false);
        AiFeedbackRequest req = new AiFeedbackRequest();
        req.setOverallScore(55);

        FeedbackAttemptDto bad = new FeedbackAttemptDto();
        bad.setQuestion("Explain transactions.");
        bad.setScore(30);
        bad.setMissingConcepts(List.of("ACID", "rollback"));
        req.setAttempts(List.of(bad));

        AiFeedbackResponse result = service.generateFeedback(req);
        assertThat(result.getWeaknesses()).contains("ACID");
    }

    @Test
    void generateFeedback_studyPlanAlwaysPresent() {
        when(properties.isConfigured()).thenReturn(false);
        AiFeedbackResponse result = service.generateFeedback(request(50, List.of(), List.of()));
        assertThat(result.getStudyPlan()).isNotBlank();
        assertThat(result.getStudyPlan()).contains("1.");
    }

    // --- helpers ---

    private AiFeedbackRequest request(int score, List<String> strengths, List<String> weaknesses) {
        AiFeedbackRequest req = new AiFeedbackRequest();
        req.setOverallScore(score);
        req.setStrengths(strengths);
        req.setWeaknesses(weaknesses);
        return req;
    }

    private void configureProperties() {
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getApiKey()).thenReturn("test-key");
        when(properties.getModel()).thenReturn("gpt-4o");
        when(properties.getBaseUrl()).thenReturn("https://api.openai.com/v1");
        when(properties.getMaxOutputTokens()).thenReturn(500);
        when(properties.supportsReasoningEffort()).thenReturn(false);
    }

    private void stubOpenAiResponse(String body) {
        when(restTemplate.exchange(any(String.class), any(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(body));
    }
}
