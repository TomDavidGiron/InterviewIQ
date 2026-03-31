package com.cvoptimizer.cv_backend.interview.rag.dto;

public class KnowledgeIngestResponse {

    private int indexedCount;

    public KnowledgeIngestResponse() {
    }

    public KnowledgeIngestResponse(int indexedCount) {
        this.indexedCount = indexedCount;
    }

    public int getIndexedCount() {
        return indexedCount;
    }

    public void setIndexedCount(int indexedCount) {
        this.indexedCount = indexedCount;
    }
}