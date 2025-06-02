package com.merlab.signals.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.ConfigPerceptronProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

/**
 * Ejemplos de Perceptrón Simple para AND, OR y XOR.
 */
public class BasicPerceptronExample {

    public static void main(String[] args) {
        // Datos de entrada: todas las combinaciones binarias
        double[][] inputs = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
        };

        // Configura perceptrones AND y OR
        NeuralNetworkProcessor andProc = new ConfigPerceptronProcessor(
            new double[]{1.0, 1.0},   // pesos
            -1.5,                       // bias
            ActivationFunctions.BINARY_STEP
        );

        NeuralNetworkProcessor orProc = new ConfigPerceptronProcessor(
            new double[]{1.0, 1.0},
            -0.5,
            ActivationFunctions.BINARY_STEP
        );

        // Ejecutar y mostrar resultados
        System.out.println("=== Perceptrón AND ===");
        runExample(andProc, inputs);

        System.out.println("\n=== Perceptrón OR ===");
        runExample(orProc, inputs);

        // XOR con un single-layer perceptron (NO linealmente separable)
        NeuralNetworkProcessor xorProc = new ConfigPerceptronProcessor(
            new double[]{1.0, 1.0},
            -0.5,
            ActivationFunctions.BINARY_STEP
        );
        System.out.println("\n=== Perceptrón XOR (falla) ===");
        runExample(xorProc, inputs);
    }

    private static void runExample(NeuralNetworkProcessor proc, double[][] inputs) {
        for (double[] in : inputs) {
            Signal sig = new Signal();
            sig.add(in[0]);
            sig.add(in[1]);
            Signal out = proc.predict(sig);
            System.out.printf("Input: [%.0f, %.0f] -> Output: %.0f%n",
                in[0], in[1], out.getValues().get(0));
        }
    }
}
