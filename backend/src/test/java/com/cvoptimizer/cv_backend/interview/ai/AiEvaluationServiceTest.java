package com.cvoptimizer.cv_backend.interview.ai;

import com.cvoptimizer.cv_backend.interview.ai.config.AiEvaluationProperties;
import com.cvoptimizer.cv_backend.interview.ai.dto.AiEvaluationRequest;
import com.cvoptimizer.cv_backend.interview.ai.dto.AiEvaluationResponse;
import com.cvoptimizer.cv_backend.interview.ai.service.AiEvaluationPromptTemplate;
import com.cvoptimizer.cv_backend.interview.ai.service.AiEvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AiEvaluationServiceTest {

    @Mock private AiEvaluationProperties properties;
    @Mock private AiEvaluationPromptTemplate promptTemplate;
    @Mock private RestTemplate restTemplate;

    private AiEvaluationService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = new AiEvaluationService(properties, promptTemplate, objectMapper, restTemplate);
        when(promptTemplate.buildDeveloperPrompt()).thenReturn("dev-prompt");
        when(promptTemplate.buildUserPrompt(any())).thenReturn("user-prompt");
    }

    @Test
    void evaluate_nullRequest_returnsEmpty() {
        assertThat(service.evaluate(null)).isEmpty();
    }

    @Test
    void evaluate_blankAnswer_returnsEmpty() {
        AiEvaluationRequest req = new AiEvaluationRequest("Explain HashMap.", List.of(), "   ", List.of());
        assertThat(service.evaluate(req)).isEmpty();
    }

    @Test
    void evaluate_notConfigured_returnsEmpty() {
        when(properties.isConfigured()).thenReturn(false);

        AiEvaluationRequest req = request("I think it uses a hash map.");
        assertThat(service.evaluate(req)).isEmpty();
    }

    @Test
    void evaluate_validResponse_parsedCorrectly() throws Exception {
        configureProperties();
        String json = objectMapper.writeValueAsString(
                validAiResponse(7, List.of("idempotency"), List.of("concurrency"), "Good answer.")
        );
        stubOpenAiResponse(outputTextJson(json));

        Optional<AiEvaluationResponse> result = service.evaluate(request("HashMap uses buckets and hashing."));

        assertThat(result).isPresent();
        assertThat(result.get().getScore()).isEqualTo(7);
        assertThat(result.get().getFeedback()).isEqualTo("Good answer.");
        assertThat(result.get().getMissingConcepts()).containsExactly("idempotency");
        assertThat(result.get().getStrengths()).containsExactly("concurrency");
    }

    @Test
    void evaluate_scoreClamped_whenAbove10() throws Exception {
        configureProperties();
        String json = objectMapper.writeValueAsString(
                validAiResponse(15, List.of(), List.of(), "Perfect.")
        );
        stubOpenAiResponse(outputTextJson(json));

        Optional<AiEvaluationResponse> result = service.evaluate(request("Great answer."));

        assertThat(result).isPresent();
        assertThat(result.get().getScore()).isEqualTo(10);
    }

    @Test
    void evaluate_scoreClamped_whenBelow0() throws Exception {
        configureProperties();
        String json = objectMapper.writeValueAsString(
                validAiResponse(-5, List.of(), List.of(), "Empty.")
        );
        stubOpenAiResponse(outputTextJson(json));

        Optional<AiEvaluationResponse> result = service.evaluate(request("Something."));

        assertThat(result).isPresent();
        assertThat(result.get().getScore()).isEqualTo(0);
    }

    @Test
    void evaluate_restClientException_returnsEmpty() {
        configureProperties();
        when(restTemplate.exchange(any(String.class), any(), any(), eq(String.class)))
                .thenThrow(new RestClientException("timeout"));

        Optional<AiEvaluationResponse> result = service.evaluate(request("Some answer."));
        assertThat(result).isEmpty();
    }

    @Test
    void evaluate_malformedJson_returnsEmpty() {
        configureProperties();
        stubOpenAiResponse("{\"output_text\": \"not-valid-json\"}");

        Optional<AiEvaluationResponse> result = service.evaluate(request("Some answer."));
        assertThat(result).isEmpty();
    }

    @Test
    void evaluate_nestedOutputFormat_parsedCorrectly() throws Exception {
        configureProperties();
        String json = objectMapper.writeValueAsString(
                validAiResponse(6, List.of(), List.of("threading"), "Decent.")
        );
        stubOpenAiResponse(nestedOutputJson(json));

        Optional<AiEvaluationResponse> result = service.evaluate(request("Threads share heap memory."));

        assertThat(result).isPresent();
        assertThat(result.get().getScore()).isEqualTo(6);
    }

    @Test
    void evaluate_nullFieldsInResponse_defaultedToEmptyCollections() throws Exception {
        configureProperties();
        String json = "{\"score\": 5, \"feedback\": \"OK\", \"missingConcepts\": null, \"strengths\": null}";
        stubOpenAiResponse(outputTextJson(json));

        Optional<AiEvaluationResponse> result = service.evaluate(request("Answer here."));

        assertThat(result).isPresent();
        assertThat(result.get().getMissingConcepts()).isNotNull().isEmpty();
        assertThat(result.get().getStrengths()).isNotNull().isEmpty();
    }

    // --- helpers ---

    private AiEvaluationRequest request(String answer) {
        return new AiEvaluationRequest("Explain HashMap.", List.of("hashing", "buckets"), answer, List.of());
    }

    private AiEvaluationResponse validAiResponse(int score, List<String> missing, List<String> strengths, String feedback) {
        AiEvaluationResponse r = new AiEvaluationResponse();
        r.setScore(score);
        r.setMissingConcepts(missing);
        r.setStrengths(strengths);
        r.setFeedback(feedback);
        return r;
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

    private String outputTextJson(String innerJson) {
        return "{\"output_text\": " + objectMapper.valueToTree(innerJson) + "}";
    }

    private String nestedOutputJson(String innerJson) throws Exception {
        return "{\"output\": [{\"content\": [{\"type\": \"output_text\", \"text\": "
                + objectMapper.valueToTree(innerJson) + "}]}]}";
    }
}
