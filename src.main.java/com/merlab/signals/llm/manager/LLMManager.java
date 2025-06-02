package com.merlab.signals.llm.manager;

import com.merlab.signals.llm.loader.LLMModelLoader;

public class LLMManager {
    private LLMModelLoader loader;

    public void setLoader(LLMModelLoader loader) {
        this.loader = loader;
    }

    public void loadModel(String path) {
        if (loader != null) loader.loadModel(path);
    }

    public String askLLM(String prompt) {
        if (loader != null) return loader.infer(prompt);
        return "No loader set.";
    }

    public void unloadModel() {
        if (loader != null) loader.unloadModel();
    }
}
