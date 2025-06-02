package com.merlab.signals.examples;

import java.util.Arrays;
import java.util.List;

public class Examples {
    public static void main(String[] args) {
    	/*
        System.out.println("== Señal Senoidal ==");
        Signal sine = SignalGenerator.generateSine(30, 1.0, 2.0);
        sine.print();

        System.out.println("\n== Señal Triangular ==");
        Signal triangle = SignalGenerator.generateTriangle(30, 1.0, 2.0);
        triangle.print();

        System.out.println("\n== Ruido Blanco ==");
        Signal noise = SignalGenerator.generateWhiteNoise(30, 1.0);
        noise.print();
       
        
        SignalGenerator generator = new SignalGenerator();
        List<Double> original = generator.generateSineWave(30, 1.0, 0.0);

        System.out.println("Original:");
        original.forEach(System.out::println);

        List<Double> normalized = SignalProcessor.normalize(original);

        System.out.println("\nNormalized:");
        normalized.forEach(System.out::println);
         
        
        List<Double> inputSignal = Arrays.asList(10.0, 15.0, 20.0, 25.0, 30.0);
        
        List<Double> normalized = SignalProcessor.normalize(inputSignal);
        
        System.out.println("Original:   " + inputSignal);
        System.out.println("Normalized: " + normalized);
        
        
        // Crear generador de señal
        SignalGenerator generator = new SignalGenerator();

        // Generar una onda senoidal de 30 muestras, amplitud 1.0, sin desfase
        List<Double> original = generator.generateSineWave(30, 1.0, 0.0);

        // Imprimir la señal original
        System.out.println("Original:");
        original.forEach(System.out::println);

        // Normalizar la señal
        List<Double> normalized2 = SignalProcessor.normalize(original);

        // Imprimir la señal normalizada
        System.out.println("\nNormalized:");
        normalized2.forEach(System.out::println);
        */
    	
    	/*
    	
        // Crear generador de señal
        SignalGenerator generator = new SignalGenerator();
        
    	
        List<Double> original = generator.generateSineWave(30, 1.0, 0.0);

        List<Double> normalized_0_1 = SignalProcessor.normalizeTo(original, 1.0);     // Normalización [0, 1]
        List<Double> normalized_0_100 = SignalProcessor.normalizeTo(original, 100.0); // Normalización [0, 100]

        
        System.out.println("Original:   " + original);
        System.out.println("Normalized: " + normalized_0_1);
        
        // Generamos una señal
        List<Double> original2 = generator.generateSineWave(30, 1.0, 0.0);

        // Decimamos la señal tomando uno de cada 3 puntos
        List<Double> decimated = SignalProcessor.decimate(original2, 3);

        // Imprimimos la señal decimada
        decimated.forEach(System.out::println);
        
        // Generamos una señal
        List<Double> original3 = generator.generateSineWave(30, 1.0, 0.0);

        // Decimamos la señal tomando cada segundo valor
        List<Double> decimated2 = SignalProcessor.decimateByTwo(original3);

        // Imprimimos la señal decimada
        decimated2.forEach(System.out::println);
        
        
    	
    	// Generamos una señal
    	List<Double> original = generator.generateSineWave(10, 1.0, 0.0);

    	// Interpolamos la señal para tener el doble de puntos
    	List<Double> interpolated = SignalProcessor.interpolate(original, 2);

    	// Imprimimos la señal interpolada
    	interpolated.forEach(System.out::println);
    	
    	*/
    	
    	/*
        // Crear el generador de señales
        SignalGenerator generator = new SignalGenerator();

        // Crear un stack para almacenar las señales
        SignalStack signalStack = new SignalStack();

        // Generar una señal de tipo seno
        List<Double> sineWave = generator.generateSine(30, 1.0, 0.0).getValues();

        // Convertir la señal en un objeto Signal
        Signal signal = new Signal(sineWave);

        // Agregar la señal al stack
        signalStack.push(signal);

        // Mostrar la última señal en el stack
        System.out.println("Última señal en el stack: ");
        signalStack.peek().print();

        // Realizar operaciones con el stack
        signalStack.pop(); // Eliminar la señal
        System.out.println("Stack vacío: " + signalStack.isEmpty());

        
        
    	//----------------------------------------
    	String yourdatabase = "test";
    	String yourusername = "root";
    	String yourpassword = "root";
        
        // Crear DatabaseManager
        DatabaseManager databaseManager = new DatabaseManager("jdbc:mariadb://localhost:3306/" + yourdatabase, yourusername, yourpassword);

        // Crear SignalManager y pasar el DatabaseManager
        SignalManager signalManager = new SignalManager(databaseManager);

        // Crear una señal de ejemplo
        Signal sineSignal = SignalGenerator.generateSine(10, 50, 1.0);

        // Agregar la señal al stack
        signalManager.addSignal(sineSignal);

        // Normalizar la última señal
        signalManager.normalizeLastSignal();

        // Guardar la última señal en la base de datos
        signalManager.saveLastSignal();

        // Mostrar el stack
        signalManager.showStack();
        
        */
        
    }
}
