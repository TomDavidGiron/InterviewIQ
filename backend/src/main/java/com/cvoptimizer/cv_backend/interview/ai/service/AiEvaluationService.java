package com.cvoptimizer.cv_backend.interview.ai.service;

import com.cvoptimizer.cv_backend.interview.ai.config.AiEvaluationProperties;
import com.cvoptimizer.cv_backend.interview.ai.dto.AiEvaluationRequest;
import com.cvoptimizer.cv_backend.interview.ai.dto.AiEvaluationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AiEvaluationService {

    private final AiEvaluationProperties properties;
    private final AiEvaluationPromptTemplate promptTemplate;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public AiEvaluationService(
            AiEvaluationProperties properties,
            AiEvaluationPromptTemplate promptTemplate,
            ObjectMapper objectMapper,
            RestTemplate restTemplate
    ) {
        this.properties = properties;
        this.promptTemplate = promptTemplate;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    public Optional<AiEvaluationResponse> evaluate(AiEvaluationRequest request) {
        if (request == null || request.getUserAnswer() == null || request.getUserAnswer().isBlank()) {
            return Optional.empty();
        }

        if (!properties.isConfigured()) {
            return Optional.empty();
        }

        try {
            String rawText = callOpenAi(request);
            if (rawText == null || rawText.isBlank()) {
                return Optional.empty();
            }

            AiEvaluationResponse parsed = objectMapper.readValue(rawText, AiEvaluationResponse.class);
            normalize(parsed);
            return Optional.of(parsed);
        } catch (RestClientException | JsonProcessingException e) {
            System.err.println("[AI_EVAL] Falling back to keyword evaluator. Reason: " + e.getMessage());
            return Optional.empty();
        }
    }

    private String callOpenAi(AiEvaluationRequest request) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", properties.getModel());
        if (properties.supportsReasoningEffort()) {
            payload.put("reasoning", Map.of("effort", properties.getReasoningEffort()));
        }
        payload.put("max_output_tokens", properties.getMaxOutputTokens());
        payload.put("input", List.of(
                Map.of("role", "developer", "content", promptTemplate.buildDeveloperPrompt()),
                Map.of("role", "user", "content", promptTemplate.buildUserPrompt(request))
        ));
        payload.put("text", Map.of(
                "format", Map.of(
                        "type", "json_schema",
                        "name", "interview_answer_evaluation",
                        "strict", true,
                        "schema", buildResponseSchema()
                )
        ));

        HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);
        ResponseEntity<String> response = restTemplate.exchange(
                properties.getBaseUrl() + "/responses",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        JsonNode root = objectMapper.readTree(response.getBody());

        JsonNode outputText = root.path("output_text");
        if (!outputText.isMissingNode() && !outputText.isNull() && !outputText.asText().isBlank()) {
            return outputText.asText();
        }

        JsonNode output = root.path("output");
        if (output.isArray()) {
            for (JsonNode item : output) {
                JsonNode content = item.path("content");
                if (!content.isArray()) {
                    continue;
                }
                for (JsonNode part : content) {
                    if ("output_text".equals(part.path("type").asText())) {
                        String text = part.path("text").asText();
                        if (!text.isBlank()) {
                            return text;
                        }
                    }
                }
            }
        }

        return null;
    }

    private Map<String, Object> buildResponseSchema() {
        Map<String, Object> propertiesNode = new LinkedHashMap<>();
        propertiesNode.put("score", Map.of("type", "integer", "minimum", 0, "maximum", 10));
        propertiesNode.put("missingConcepts", Map.of(
                "type", "array",
                "items", Map.of("type", "string")
        ));
        propertiesNode.put("strengths", Map.of(
                "type", "array",
                "items", Map.of("type", "string")
        ));
        propertiesNode.put("feedback", Map.of("type", "string"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("additionalProperties", false);
        schema.put("properties", propertiesNode);
        schema.put("required", List.of("score", "missingConcepts", "strengths", "feedback"));
        return schema;
    }

    private void normalize(AiEvaluationResponse response) {
        if (response == null) {
            return;
        }
        if (response.getScore() < 0) {
            response.setScore(0);
        }
        if (response.getScore() > 10) {
            response.setScore(10);
        }
        if (response.getMissingConcepts() == null) {
            response.setMissingConcepts(List.of());
        }
        if (response.getStrengths() == null) {
            response.setStrengths(List.of());
        }
        if (response.getFeedback() == null) {
            response.setFeedback("");
        }
    }
}
