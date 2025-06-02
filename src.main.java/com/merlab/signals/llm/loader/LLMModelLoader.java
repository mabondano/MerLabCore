package com.merlab.signals.llm.loader;

/**
 * Interfaz para cargar e inferir modelos LLM.
 */
public interface LLMModelLoader {
    void loadModel(String path);
    String infer(String prompt);
    String infer(String prompt, int maxTokens, double temperature);
    void unloadModel();
}
