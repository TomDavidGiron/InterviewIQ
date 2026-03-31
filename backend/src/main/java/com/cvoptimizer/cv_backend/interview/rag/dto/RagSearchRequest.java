package com.cvoptimizer.cv_backend.interview.rag.dto;

public class RagSearchRequest {

    private String query;
    private Integer limit;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}