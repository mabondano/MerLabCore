package com.merlab.signals.llm.loader;

/**
 * Loader educativo/manual (mini-LLM).
 */
public class MiniLLMLoader implements LLMModelLoader {
    @Override
    public void loadModel(String path) { /* TODO: Implementar */ }

    @Override
    public String infer(String prompt) { /* TODO: Implementar */ return "Not implemented"; }
    
    @Override
    public String infer(String prompt, int maxTokens, double temperature) {
    	 /* TODO: Implementar */ 
        return "Not implemented";
    }

    @Override
    public void unloadModel() { /* TODO: Implementar */ }
}
