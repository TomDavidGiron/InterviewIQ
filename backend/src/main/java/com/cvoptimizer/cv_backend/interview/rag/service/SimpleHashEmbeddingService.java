package com.cvoptimizer.cv_backend.interview.rag.service;

import com.cvoptimizer.cv_backend.interview.rag.config.RagProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class SimpleHashEmbeddingService implements EmbeddingService {

    private final RagProperties ragProperties;

    public SimpleHashEmbeddingService(RagProperties ragProperties) {
        this.ragProperties = ragProperties;
    }

    @Override
    public float[] embed(String text) {
        int dim = ragProperties.getEmbeddingDimension();
        float[] vector = new float[dim];

        if (text == null || text.isBlank()) {
            return vector;
        }

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

        if (sumSquares == 0.0) {
            return;
        }

        float norm = (float) Math.sqrt(sumSquares);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] / norm;
        }
    }
}