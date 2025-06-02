package com.merlab.signals.examples;

import com.merlab.signals.llm.loader.LLMModelLoader;
import com.merlab.signals.llm.loader.MiniLLMLoader;
import com.merlab.signals.llm.manager.LLMManager;
import com.merlab.signals.llm.model.LLMRequest;
import com.merlab.signals.llm.model.LLMResponse;

/**
 * Ejemplo de uso que imprime tanto el request como el response.
 */
public class LLMExampleDemo2 {
    public static void main(String[] args) {
        // Usar un loader mock (educativo)
        LLMModelLoader loader = new MiniLLMLoader();
        LLMManager llmManager = new LLMManager();
        llmManager.setLoader(loader);
        llmManager.loadModel("mock-model-path");

        // Crear el request usando el builder
        LLMRequest request = new LLMRequest.Builder()
                .prompt("¿Cuál es el número pi con 3 decimales?")
                .maxTokens(10)
                .temperature(0.9)
                .build();

        // Mostrar el request
        System.out.println("------ REQUEST ------");
        System.out.println(request);

        // Simular inferencia (el mock puede devolver texto fijo)
        String mockText = "El número pi es 3.142.";
        LLMResponse response = new LLMResponse.Builder()
                .generatedText(mockText)
                .elapsedTimeMs(20)
                .tokensUsed(7)
                .build();

        // Mostrar el response
        System.out.println("------ RESPONSE ------");
        System.out.println(response);

        // Descargar el modelo (mock)
        llmManager.unloadModel();
    }
}
