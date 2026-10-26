package com.cvoptimizer.cv_backend.interview.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rag")
public class RagProperties {

    private int embeddingDimension = 1536;
    private int searchDefaultLimit = 5;
    // Cosine similarity floor; text-embedding-3-small scores unrelated text roughly 0.0–0.25
    private double searchMinSimilarity = 0.30;
    private boolean bootstrapEnabled = true;

    public int getEmbeddingDimension() {
        return embeddingDimension;
    }

    public void setEmbeddingDimension(int embeddingDimension) {
        this.embeddingDimension = embeddingDimension;
    }

    public int getSearchDefaultLimit() {
        return searchDefaultLimit;
    }

    public void setSearchDefaultLimit(int searchDefaultLimit) {
        this.searchDefaultLimit = searchDefaultLimit;
    }

    public boolean isBootstrapEnabled() {
        return bootstrapEnabled;
    }

    public void setBootstrapEnabled(boolean bootstrapEnabled) {
        this.bootstrapEnabled = bootstrapEnabled;
    }

    public double getSearchMinSimilarity() {
        return searchMinSimilarity;
    }

    public void setSearchMinSimilarity(double searchMinSimilarity) {
        this.searchMinSimilarity = searchMinSimilarity;
    }
}