package com.cvoptimizer.cv_backend.interview.feedback.service;

import com.cvoptimizer.cv_backend.interview.ai.config.AiEvaluationProperties;
import com.cvoptimizer.cv_backend.interview.feedback.dto.AiFeedbackRequest;
import com.cvoptimizer.cv_backend.interview.feedback.dto.AiFeedbackResponse;
import com.cvoptimizer.cv_backend.interview.feedback.dto.FeedbackAttemptDto;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AiFeedbackService {

    private final AiEvaluationProperties properties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public AiFeedbackService(AiEvaluationProperties properties, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    public AiFeedbackResponse generateFeedback(AiFeedbackRequest request) {
        if (request == null) {
            return fallbackFeedback(new AiFeedbackRequest());
        }

        if (properties.isConfigured()) {
            try {
                String rawText = callOpenAi(request);
                if (rawText != null && !rawText.isBlank()) {
                    AiFeedbackResponse parsed = objectMapper.readValue(rawText, AiFeedbackResponse.class);
                    return normalize(parsed, request);
                }
            } catch (RestClientException | JsonProcessingException e) {
                System.err.println("[AI_FEEDBACK] Falling back to rule-based summary. Reason: " + e.getMessage());
            }
        }

        return fallbackFeedback(request);
    }

    private String callOpenAi(AiFeedbackRequest request) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", properties.getModel());
        if (properties.supportsReasoningEffort()) {
            payload.put("reasoning", Map.of("effort", properties.getReasoningEffort()));
        }
        payload.put("max_output_tokens", Math.max(500, properties.getMaxOutputTokens()));
        payload.put("input", List.of(
                Map.of("role", "developer", "content", buildDeveloperPrompt(resolveRole(request))),
                Map.of("role", "user", "content", buildUserPrompt(request))
        ));
        payload.put("text", Map.of(
                "format", Map.of(
                        "type", "json_schema",
                        "name", "interview_summary_feedback",
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

    private String resolveRole(AiFeedbackRequest request) {
        if (request == null || request.getRoleHint() == null || request.getRoleHint().isBlank()) {
            return "backend";
        }
        return request.getRoleHint().trim();
    }

    private String buildDeveloperPrompt(String role) {
        return """
                You are an expert %s interview coach.
                Return only valid JSON.
                Analyze the interview summary and produce concise, useful feedback.
                Keep strengths and weaknesses short and concrete.
                Prefer skill names or concepts, not long sentences.
                The studyPlan should be a short multi-line plan with 3 numbered steps.
                """.formatted(role);
    }

    private String buildUserPrompt(AiFeedbackRequest request) throws JsonProcessingException {
        return """
                Build interview feedback from this data.

                %s
                """.formatted(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
    }

    private Map<String, Object> buildResponseSchema() {
        Map<String, Object> propertiesNode = new LinkedHashMap<>();
        propertiesNode.put("diagnosis", Map.of("type", "string"));
        propertiesNode.put("strengths", Map.of("type", "array", "items", Map.of("type", "string")));
        propertiesNode.put("weaknesses", Map.of("type", "array", "items", Map.of("type", "string")));
        propertiesNode.put("studyPlan", Map.of("type", "string"));
        propertiesNode.put("feedbackSummary", Map.of("type", "string"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("additionalProperties", false);
        schema.put("properties", propertiesNode);
        schema.put("required", List.of("diagnosis", "strengths", "weaknesses", "studyPlan", "feedbackSummary"));
        return schema;
    }

    private AiFeedbackResponse fallbackFeedback(AiFeedbackRequest request) {
        AiFeedbackResponse response = new AiFeedbackResponse();

        List<String> strengths = firstNonEmpty(
                cleanList(request.getStrengths()),
                inferStrengthsFromAttempts(request.getAttempts())
        );

        List<String> weaknesses = firstNonEmpty(
                cleanList(request.getWeaknesses()),
                cleanList(request.getWeakTopics()),
                inferWeaknessesFromAttempts(request.getAttempts())
        );

        int score = Math.max(0, Math.min(100, request.getOverallScore()));
        boolean strong = score >= 85;
        boolean okay = score >= 70;

        String role = resolveRole(request);

        String diagnosis;
        if (strong) {
            diagnosis = "Candidate shows strong " + role + " fundamentals and is close to interview-ready.";
        } else if (okay) {
            diagnosis = "Candidate shows solid fundamentals but still has noticeable gaps in weaker " + role + " areas.";
        } else {
            diagnosis = "Candidate needs more practice before a " + role + " interview, especially in weaker concepts and answer depth.";
        }

        String summary;
        if (!weaknesses.isEmpty()) {
            summary = "Good progress, but focus next on: " + String.join(", ", weaknesses);
        } else if (!strengths.isEmpty()) {
            summary = "Good progress. Strongest areas so far: " + String.join(", ", strengths);
        } else {
            summary = "Interview completed. Keep practicing structured " + role + " answers with concrete examples.";
        }

        String studyPlan = buildStudyPlan(strengths, weaknesses, role);

        response.setDiagnosis(diagnosis);
        response.setStrengths(strengths);
        response.setWeaknesses(weaknesses);
        response.setStudyPlan(studyPlan);
        response.setFeedbackSummary(summary);
        return normalize(response, request);
    }

    private String buildStudyPlan(List<String> strengths, List<String> weaknesses, String role) {
        List<String> focus = !weaknesses.isEmpty() ? weaknesses : strengths;
        List<String> top = focus.stream().limit(3).toList();

        String first = top.isEmpty()
                ? "Review the weakest interview topics from this session"
                : "Review these topics first: " + String.join(", ", top);

        return """
                1. %s
                2. Practice 5–10 interview questions and answer them aloud with one concrete %s example each
                3. Re-run the interview and check whether the same weak areas still repeat
                """.formatted(first, role);
    }

    private List<String> inferStrengthsFromAttempts(List<FeedbackAttemptDto> attempts) {
        if (attempts == null || attempts.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<String> strengths = new LinkedHashSet<>();
        for (FeedbackAttemptDto attempt : attempts) {
            if (attempt == null) {
                continue;
            }
            if (attempt.getScore() >= 80 && attempt.getQuestion() != null && !attempt.getQuestion().isBlank()) {
                strengths.add(shortenQuestion(attempt.getQuestion()));
            }
            if (strengths.size() >= 3) {
                break;
            }
        }
        return new ArrayList<>(strengths);
    }

    private List<String> inferWeaknessesFromAttempts(List<FeedbackAttemptDto> attempts) {
        if (attempts == null || attempts.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<String> weaknesses = new LinkedHashSet<>();
        for (FeedbackAttemptDto attempt : attempts) {
            if (attempt == null) {
                continue;
            }
            if (attempt.getMissingConcepts() != null) {
                for (String missing : attempt.getMissingConcepts()) {
                    if (missing != null && !missing.isBlank()) {
                        weaknesses.add(missing.trim());
                    }
                    if (weaknesses.size() >= 3) {
                        break;
                    }
                }
            }
            if (weaknesses.size() >= 3) {
                break;
            }
        }
        return new ArrayList<>(weaknesses);
    }

    private String shortenQuestion(String question) {
        String cleaned = question == null ? "" : question.replaceAll("\\s+", " ").trim();
        if (cleaned.length() <= 48) {
            return cleaned;
        }
        return cleaned.substring(0, 48).trim() + "...";
    }

    @SafeVarargs
    private final List<String> firstNonEmpty(List<String>... candidates) {
        for (List<String> candidate : candidates) {
            List<String> cleaned = cleanList(candidate);
            if (!cleaned.isEmpty()) {
                return cleaned;
            }
        }
        return List.of();
    }

    private List<String> cleanList(List<String> values) {
        if (values == null) {
            return List.of();
        }
        LinkedHashSet<String> cleaned = new LinkedHashSet<>();
        for (String value : values) {
            if (value == null) {
                continue;
            }
            String trimmed = value.trim();
            if (!trimmed.isBlank()) {
                cleaned.add(trimmed);
            }
        }
        return new ArrayList<>(cleaned);
    }

    private AiFeedbackResponse normalize(AiFeedbackResponse response, AiFeedbackRequest request) {
        if (response == null) {
            response = new AiFeedbackResponse();
        }

        String role = resolveRole(request);

        if (response.getDiagnosis() == null || response.getDiagnosis().isBlank()) {
            response.setDiagnosis("Interview completed. Review the weak areas and practice more " + role + " examples.");
        }
        response.setStrengths(firstNonEmpty(response.getStrengths(), request.getStrengths()));
        response.setWeaknesses(firstNonEmpty(response.getWeaknesses(), request.getWeaknesses(), request.getWeakTopics()));

        if (response.getStudyPlan() == null || response.getStudyPlan().isBlank()) {
            response.setStudyPlan(buildStudyPlan(response.getStrengths(), response.getWeaknesses(), role));
        }
        if (response.getFeedbackSummary() == null || response.getFeedbackSummary().isBlank()) {
            response.setFeedbackSummary("Interview completed. Review weak topics and keep practicing structured " + role + " answers.");
        }

        response.setStrengths(cleanList(response.getStrengths()));
        response.setWeaknesses(cleanList(response.getWeaknesses()));
        return response;
    }
}
