/*
 * Copyright 2025 Merly Abondano
 *
 * Created:   2025-06-07
 * Author:    Merly Abondano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
