package com.cvoptimizer.cv_backend.interview.rag.service;

import java.util.List;

public interface EmbeddingService {
    float[] embed(String text);
    boolean isConfigured();

    /**
     * Embeds several texts in one call, in input order. Unlike {@link #embed(String)} this never
     * falls back to placeholder vectors — it throws, so callers never persist meaningless embeddings.
     */
    List<float[]> embedBatch(List<String> texts);

    /** Identifies the embedding model, so stored vectors can be re-embedded when it changes. */
    String modelName();
}
