package com.cvoptimizer.cv_backend.interview.rag.service;

public interface EmbeddingService {
    float[] embed(String text);
}