package com.cvoptimizer.cv_backend.interview.rag.model;

public class KnowledgeChunk {

    private Long id;
    private String chunkKey;
    private String content;
    private String topic;
    private String difficulty;
    private String source;
    private String sourceType;
    private String metadataJson;
    private double similarity;

    public KnowledgeChunk() {
    }

    public KnowledgeChunk(
            Long id,
            String chunkKey,
            String content,
            String topic,
            String difficulty,
            String source,
            String sourceType,
            String metadataJson,
            double similarity
    ) {
        this.id = id;
        this.chunkKey = chunkKey;
        this.content = content;
        this.topic = topic;
        this.difficulty = difficulty;
        this.source = source;
        this.sourceType = sourceType;
        this.metadataJson = metadataJson;
        this.similarity = similarity;
    }

    public Long getId() {
        return id;
    }

    public String getChunkKey() {
        return chunkKey;
    }

    public String getContent() {
        return content;
    }

    public String getTopic() {
        return topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getSource() {
        return source;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setChunkKey(String chunkKey) {
        this.chunkKey = chunkKey;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
}