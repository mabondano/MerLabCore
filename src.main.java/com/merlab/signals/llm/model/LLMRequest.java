package com.merlab.signals.llm.model;

//Builder Pattern

public class LLMRequest {
    private final String prompt;
    private final int maxTokens;
    private final double temperature;

    private LLMRequest(Builder builder) {
        this.prompt = builder.prompt;
        this.maxTokens = builder.maxTokens;
        this.temperature = builder.temperature;
    }

    // Getters
    public String getPrompt() { return prompt; }
    public int getMaxTokens() { return maxTokens; }
    public double getTemperature() { return temperature; }
    
    // toString para logging bonito
    @Override
    public String toString() {
        return "LLMRequest {\n" +
               "  prompt: \"" + prompt + "\",\n" +
               "  maxTokens: " + maxTokens + ",\n" +
               "  temperature: " + temperature + "\n" +
               "}";
    }

    // Builder interno
    public static class Builder {
        private String prompt;
        private int maxTokens = 32; // Valor por defecto
        private double temperature = 1.0; // Valor por defecto

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder maxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public LLMRequest build() {
            return new LLMRequest(this);
        }
    }
}

/*
 * LLMRequest request = new LLMRequest.Builder()
    .prompt("¿Cuál es la capital de Alemania?")
    .maxTokens(20)
    .temperature(0.7)
    .build();
    
    System.out.println(request);
 * 
 */
