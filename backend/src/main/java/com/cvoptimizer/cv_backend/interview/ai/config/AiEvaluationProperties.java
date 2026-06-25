package com.cvoptimizer.cv_backend.interview.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ai.evaluation")
public class AiEvaluationProperties {

    private boolean enabled = false;
    private String apiKey;
    private String model = "gpt-4o";
    private String baseUrl = "https://api.openai.com/v1";
    private Integer maxContextItems = 3;
    private Integer maxOutputTokens = 500;
    private String reasoningEffort = "low";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Integer getMaxContextItems() {
        return maxContextItems;
    }

    public void setMaxContextItems(Integer maxContextItems) {
        this.maxContextItems = maxContextItems;
    }

    public Integer getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(Integer maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public String getReasoningEffort() {
        return reasoningEffort;
    }

    public void setReasoningEffort(String reasoningEffort) {
        this.reasoningEffort = reasoningEffort;
    }

    public boolean isConfigured() {
        return enabled && apiKey != null && !apiKey.isBlank();
    }

    /**
     * The "reasoning" parameter is only accepted by OpenAI's reasoning models
     * (o-series, gpt-5 family). Sending it to gpt-4o etc. causes a 400 error.
     */
    public boolean supportsReasoningEffort() {
        if (model == null) return false;
        String m = model.toLowerCase();
        return m.startsWith("o1") || m.startsWith("o3") || m.startsWith("o4") || m.startsWith("gpt-5");
    }
}
