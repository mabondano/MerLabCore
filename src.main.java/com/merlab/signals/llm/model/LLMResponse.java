package com.merlab.signals.llm.model;

public class LLMResponse {
    private final String generatedText;
    private final long elapsedTimeMs; // Por ejemplo, tiempo de inferencia
    private final int tokensUsed;     // Por ejemplo, número de tokens generados

    private LLMResponse(Builder builder) {
        this.generatedText = builder.generatedText;
        this.elapsedTimeMs = builder.elapsedTimeMs;
        this.tokensUsed = builder.tokensUsed;
    }

    // Getters
    public String getGeneratedText() { return generatedText; }
    public long getElapsedTimeMs() { return elapsedTimeMs; }
    public int getTokensUsed() { return tokensUsed; }
    
    // Nuevo: Método para imprimir de forma bonita
    @Override
    public String toString() {
        return "LLMResponse {\n" +
               "  generatedText: \"" + generatedText + "\",\n" +
               "  elapsedTimeMs: " + elapsedTimeMs + ",\n" +
               "  tokensUsed: " + tokensUsed + "\n" +
               "}";
    }

    // Builder interno
    public static class Builder {
        private String generatedText;
        private long elapsedTimeMs = -1; // Valor por defecto
        private int tokensUsed = -1;     // Valor por defecto

        public Builder generatedText(String generatedText) {
            this.generatedText = generatedText;
            return this;
        }

        public Builder elapsedTimeMs(long elapsedTimeMs) {
            this.elapsedTimeMs = elapsedTimeMs;
            return this;
        }

        public Builder tokensUsed(int tokensUsed) {
            this.tokensUsed = tokensUsed;
            return this;
        }

        public LLMResponse build() {
            return new LLMResponse(this);
        }
    }
}

/*
*  LLMResponse response = new LLMResponse.Builder()
    .generatedText("La capital de Alemania es Berlín.")
    .elapsedTimeMs(45)
    .tokensUsed(7)
    .build();
    
    System.out.println(response);
*
**/
