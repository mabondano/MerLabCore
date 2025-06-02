package com.merlab.signals.examples;

import com.merlab.signals.llm.loader.LLMModelLoader;
import com.merlab.signals.llm.loader.MiniLLMLoader;
import com.merlab.signals.llm.manager.LLMManager;
import com.merlab.signals.llm.model.LLMRequestSimple;
import com.merlab.signals.llm.model.LLMResponse;

/**
 * Ejemplo simple de uso del módulo LLM.
 */
public class LLMExampleDemo {

    public static void main(String[] args) {
        // Crear un mock del loader (implementación educativa)
        LLMModelLoader loader = new MiniLLMLoader();

        // Crear el manager y asignar el loader
        LLMManager llmManager = new LLMManager();
        llmManager.setLoader(loader);

        // (Opcional) cargar modelo
        llmManager.loadModel("ruta/al/modelo/fake-llm-model");

        // Crear request de ejemplo
        LLMRequestSimple request = new LLMRequestSimple();
        request.setPrompt("¿Cuál es la capital de Francia?");
        request.setMaxTokens(20);
        request.setTemperature(0.7);

        // Simular inferencia (realmente es un mock)
        String respuesta = llmManager.askLLM(request.getPrompt());

        // Mostrar resultado simulado
        System.out.println("Prompt: " + request.getPrompt());
        System.out.println("Respuesta del LLM: " + respuesta);

        // Descargar el modelo (mock)
        llmManager.unloadModel();
    }
}
