package com.cvoptimizer.cv_backend.interview.rag.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RagSearchRequest {

    @NotBlank(message = "query is required")
    private String query;

    @Min(value = 1, message = "limit must be at least 1")
    @Max(value = 50, message = "limit must be at most 50")
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