package com.cvoptimizer.cv_backend.interview.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rag")
public class RagProperties {

    private int embeddingDimension = 1536;
    private int searchDefaultLimit = 5;
    private boolean bootstrapEnabled = true;
    private String bootstrapSource = "question_bank";

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

    public String getBootstrapSource() {
        return bootstrapSource;
    }

    public void setBootstrapSource(String bootstrapSource) {
        this.bootstrapSource = bootstrapSource;
    }
}