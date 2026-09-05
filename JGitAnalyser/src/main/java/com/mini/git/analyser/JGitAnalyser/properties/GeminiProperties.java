package com.mini.git.analyser.JGitAnalyser.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

    private String apiKey;
    private String model;
    private boolean isGeminiEnabled;

    public GeminiProperties() {

    }

    public GeminiProperties(String apiKey, String model, boolean isGeminiEnabled) {
        this.apiKey = apiKey;
        this.model = model;
        this.isGeminiEnabled = isGeminiEnabled;
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

    public boolean isGeminiEnabled() {
        return isGeminiEnabled;
    }

    public void setGeminiEnabled(boolean geminiEnabled) {
        isGeminiEnabled = geminiEnabled;
    }
}
