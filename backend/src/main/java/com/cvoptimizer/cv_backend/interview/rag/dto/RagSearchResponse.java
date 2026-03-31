package com.cvoptimizer.cv_backend.interview.rag.dto;

import java.util.List;

public class RagSearchResponse {

    private String query;
    private List<KnowledgeChunkDto> chunks;
    private List<String> context;

    public RagSearchResponse() {
    }

    public RagSearchResponse(String query, List<KnowledgeChunkDto> chunks, List<String> context) {
        this.query = query;
        this.chunks = chunks;
        this.context = context;
    }

    public String getQuery() {
        return query;
    }

    public List<KnowledgeChunkDto> getChunks() {
        return chunks;
    }

    public List<String> getContext() {
        return context;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setChunks(List<KnowledgeChunkDto> chunks) {
        this.chunks = chunks;
    }

    public void setContext(List<String> context) {
        this.context = context;
    }
}