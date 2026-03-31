package com.cvoptimizer.cv_backend.interview.rag.dto;

public class KnowledgeChunkDto {

    private Long id;
    private String content;
    private String topic;
    private String difficulty;
    private String source;
    private String sourceType;
    private double similarity;

    public KnowledgeChunkDto() {
    }

    public KnowledgeChunkDto(
            Long id,
            String content,
            String topic,
            String difficulty,
            String source,
            String sourceType,
            double similarity
    ) {
        this.id = id;
        this.content = content;
        this.topic = topic;
        this.difficulty = difficulty;
        this.source = source;
        this.sourceType = sourceType;
        this.similarity = similarity;
    }

    public Long getId() {
        return id;
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

    public double getSimilarity() {
        return similarity;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
}