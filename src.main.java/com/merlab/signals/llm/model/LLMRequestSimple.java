package com.merlab.signals.llm.model;

public class LLMRequestSimple {
    private String prompt;
    private int maxTokens = 32;       // Valor por defecto, si quieres
    private double temperature = 1.0; // Valor por defecto, si quieres

    public LLMRequestSimple() {}

    // Setters
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    // Getters
    public String getPrompt() {
        return prompt;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }
}
