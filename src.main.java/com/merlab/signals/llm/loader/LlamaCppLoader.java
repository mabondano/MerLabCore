package com.merlab.signals.llm.loader;

/**
 * Loader que utiliza JNI/JNA para llamar a llama.cpp.
 */
public class LlamaCppLoader implements LLMModelLoader {
	
    // Carga la librería nativa al iniciar la clase
    static {
        System.loadLibrary("LlamaCppLoader"); // Se busca LlamaCppLoader.dll en el PATH
    }
    
    // Métodos nativos (deben tener implementación en C)
    public native long initModel(String modelPath);
    public native String infer(long modelPtr, String prompt, int maxTokens, double temperature);
    public native void freeModel(long modelPtr);

    // Implementación del interface (simplificada)
    private long modelPtr = 0;
    
    @Override
    public void loadModel(String path) {
        modelPtr = initModel(path);
    }

    @Override
    public String infer(String prompt) {
        // Hardcodeamos params para empezar, luego puedes conectar con LLMRequest
        return infer(modelPtr, prompt, 64, 0.7);
    }
    
    @Override
    public String infer(String prompt, int maxTokens, double temperature) {
        return infer(modelPtr, prompt, maxTokens, temperature);
    }

    @Override
    public void unloadModel() {
        freeModel(modelPtr);
        modelPtr = 0;
    }
}
