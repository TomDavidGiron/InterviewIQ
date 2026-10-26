package com.cvoptimizer.cv_backend.interview.rag.service;

import com.cvoptimizer.cv_backend.interview.ai.config.AiEvaluationProperties;
import com.cvoptimizer.cv_backend.interview.rag.config.RagProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class OpenAiEmbeddingService implements EmbeddingService {

    private static final String EMBEDDING_MODEL = "text-embedding-3-small";
    private static final String EMBEDDINGS_URL = "https://api.openai.com/v1/embeddings";

    private final AiEvaluationProperties aiProperties;
    private final RagProperties ragProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenAiEmbeddingService(AiEvaluationProperties aiProperties, RagProperties ragProperties) {
        this.aiProperties = aiProperties;
        this.ragProperties = ragProperties;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean isConfigured() {
        return aiProperties.isConfigured();
    }

    @Override
    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            return new float[ragProperties.getEmbeddingDimension()];
        }

        if (aiProperties.isConfigured()) {
            try {
                return callOpenAiEmbedding(text);
            } catch (RestClientException | IllegalStateException e) {
                System.err.println("[EMBEDDING] OpenAI call failed, using hash fallback. Reason: " + e.getMessage());
            }
        }

        return hashEmbed(text);
    }

    @Override
    public String modelName() {
        return EMBEDDING_MODEL;
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return List.of();
        }
        if (!aiProperties.isConfigured()) {
            throw new IllegalStateException("OpenAI API key is not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.getApiKey());

        Map<String, Object> payload = Map.of("model", EMBEDDING_MODEL, "input", texts);
        String response = restTemplate.postForObject(EMBEDDINGS_URL, new HttpEntity<>(payload, headers), String.class);

        try {
            JsonNode data = objectMapper.readTree(response).path("data");
            float[][] vectors = new float[texts.size()][];
            for (JsonNode item : data) {
                // Each result carries its input index; don't rely on response order
                vectors[item.path("index").asInt()] = toVector(item.path("embedding"));
            }
            for (int i = 0; i < vectors.length; i++) {
                if (vectors[i] == null) {
                    throw new IllegalStateException("Embedding response is missing input " + i);
                }
            }
            return List.of(vectors);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse batch embedding response", e);
        }
    }

    private float[] toVector(JsonNode embeddingNode) {
        float[] vector = new float[embeddingNode.size()];
        for (int i = 0; i < embeddingNode.size(); i++) {
            vector[i] = (float) embeddingNode.get(i).asDouble();
        }
        return vector;
    }

    private float[] callOpenAiEmbedding(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.getApiKey());

        Map<String, Object> payload = Map.of(
                "model", EMBEDDING_MODEL,
                "input", text
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        String response = restTemplate.postForObject(EMBEDDINGS_URL, request, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode embeddingNode = root.path("data").get(0).path("embedding");

            float[] vector = new float[embeddingNode.size()];
            for (int i = 0; i < embeddingNode.size(); i++) {
                vector[i] = (float) embeddingNode.get(i).asDouble();
            }
            return vector;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse embedding response", e);
        }
    }

    // Fallback when API key is not configured
    private float[] hashEmbed(String text) {
        int dim = ragProperties.getEmbeddingDimension();
        float[] vector = new float[dim];

        String normalized = text.toLowerCase(Locale.ROOT).trim();
        String[] tokens = normalized.split("\\s+");

        for (String token : tokens) {
            byte[] bytes = token.getBytes(StandardCharsets.UTF_8);
            int hash = 17;
            for (byte b : bytes) {
                hash = 31 * hash + b;
            }
            int idx = Math.floorMod(hash, dim);
            vector[idx] += 1.0f;
        }

        normalize(vector);
        return vector;
    }

    private void normalize(float[] vector) {
        double sumSquares = 0.0;
        for (float v : vector) {
            sumSquares += v * v;
        }
        if (sumSquares == 0.0) return;
        float norm = (float) Math.sqrt(sumSquares);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] / norm;
        }
    }
}
