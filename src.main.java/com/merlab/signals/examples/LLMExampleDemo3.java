package com.merlab.signals.examples;

import com.merlab.signals.llm.loader.LlamaCppLoader;

public class LLMExampleDemo3 {
    public static void main(String[] args) {
        LlamaCppLoader loader = new LlamaCppLoader();
        try {
            loader.loadModel("C:/merLab/ai/models/tinyllama-1.1b-chat-v1.0.Q4_0.gguf");
            //loader.loadModel("C:/merLab/ai/models/starcoder2-7b.Q4_K_M.gguf");
            System.out.println("¡Carga del modelo exitosa!");
        } catch (RuntimeException e) {
            System.err.println("¡Error al cargar el modelo! " + e.getMessage());
            e.printStackTrace();
        }
        
        //
        //String prompt = "Hello world!";
        //String prompt = "<|user|>\nHola mundo\n<|assistant|>\n";
        //String respuesta = loader.infer(prompt);
        //String prompt = "Escribe tres oraciones poéticas sobre el atardecer.";
        //String prompt = "Dame tres ejemplos de frases que incluyan la palabra 'perro'.";
        //String respuesta = loader.infer(prompt, 150, 0.7); // O el número de tokens y temperatura que quieras
        //String prompt = "<|user|>\nHola, ¿puedes explicarme qué significa 'Hola mundo' en español?\n<|assistant|>\n";
        // Definición simple
        //String prompt = "¿Qué significa la palabra 'programador'?";

        // Matemática sencilla
        //String prompt = "¿Cuánto es 13 + 27?";

        // Capital de país
        //String prompt = "¿Cuál es la capital de Alemania?";

        // Código JavaScript
        //String prompt = "Escribe una línea de código JavaScript que imprima \"Hola Mundo\" en la consola.";
        //String prompt = "Escribe un código en JavaScript que imprima 'Hola mundo' y añade un comentario que explique el código. Solo muestra el código, sin explicaciones.";
        //String prompt = "Gib mir ein Java-Programm, das 'Hallo Welt' ausgibt, mit einem Kommentar auf Deutsch.";
        
        // Simple explanation
        //String prompt = "What does the word 'developer' mean?";

        // Simple math
        //String prompt = "What is 13 plus 27?";

        // Capital city
        String prompt = "What is the capital of Germany?";

        // JavaScript code
        //String prompt = "Write a single line of JavaScript code that prints \"Hello World\" to the console.";
        
        // Einfache Erklärung
        //String prompt = "Was bedeutet das Wort 'Entwickler'?";

        // Matheaufgabe
        //String prompt = "Was ist 13 plus 27?";

        // Hauptstadtfrage
        //String prompt = "Was ist die Hauptstadt von Deutschland?";

        // JavaScript-Code
        //String prompt = "Schreibe eine einzelne JavaScript-Zeile, die \"Hallo Welt\" auf der Konsole ausgibt.";





        String respuesta = loader.infer(prompt, 200, 0.7);

        System.out.println("Respuesta del modelo: " + respuesta);
        //
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
