package com.merlab.llm.examples;

import com.merlab.signals.llm.loader.LlamaCppLoader;

public class LLMExampleDemo3 {
    public static void main(String[] args) {
        LlamaCppLoader loader = new LlamaCppLoader();
        try {
            loader.loadModel("C:/Users/merly.abondano/Work/models/tinyllama-1.1b-chat-v1.0.Q4_0.gguf");
            System.out.println("¡Carga del modelo exitosa!");
        } catch (RuntimeException e) {
            System.err.println("¡Error al cargar el modelo! " + e.getMessage());
            e.printStackTrace();
        }
        loader.unloadModel();
        System.out.println("¡Modelo descargado!");
    }
}
/*
public class LLMExampleDemo3 {
	
    public static void main(String[] args) {
        LlamaCppLoader loader = new LlamaCppLoader();
        //loader.loadModel("ruta/a/tu/modelo/model.gguf"); // ¡Pon aquí la ruta real!
        //loader.loadModel("C:\\Users\\merly.abondano\\Work\\models\\tinyllama-1.1b-chat-v1.0.Q4_0.gguf");
        try {
            loader.loadModel("C:/Users/merly.abondano/Work/models/tinyllama-1.1b-chat-v1.0.Q4_0.gguf");
        } catch (RuntimeException e) {
            System.err.println("¡Error al cargar el modelo! " + e.getMessage());
            e.printStackTrace();
        }
        String prompt = "¡Hola, Merly!";
        String respuesta = loader.infer(prompt);
        System.out.println("Respuesta del modelo: " + respuesta);
        loader.unloadModel();
    }
}
*/
