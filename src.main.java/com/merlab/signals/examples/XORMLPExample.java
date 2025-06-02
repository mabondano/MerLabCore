package com.merlab.signals.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

/**
 * Ejemplo de un Perceptrón Multicapa (MLP) con pesos manuales que implementa XOR.
 */
public class XORMLPExample {

    public static void main(String[] args) {
        // 1) Definimos las 2 capas del MLP:
        //    Capa oculta con 2 neuronas (aprox. OR y NAND):
        double[][] w1 = {
            { 10.0,  10.0},   // Neurona 1 (OR-like)
            {-10.0, -10.0}    // Neurona 2 (NAND-like)
        };
        double[] b1 = { -5.0, 15.0 };

        //    Capa de salida con 1 neurona (AND-like sobre las 2 salidas anteriores):
        double[][] w2 = {
            { 10.0, 10.0 }
        };
        double[] b2 = { -15.0 };

        // 2) Construimos las capas con función sigmoid
        Layer hidden = new Layer(w1, b1, ActivationFunctions.SIGMOID);
        Layer output = new Layer(w2, b2, ActivationFunctions.SIGMOID);

        // 3) Preparamos el procesador MLP
        NeuralNetworkProcessor mlp = new MultiLayerPerceptronProcessor(
            java.util.List.of(hidden, output)
        );

        // 4) Probamos las cuatro combinaciones de XOR
        double[][] inputs = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
        };

        System.out.println("MLP XOR Approximation:");
        for (double[] inp : inputs) {
            Signal s = new Signal();
            s.add(inp[0]);
            s.add(inp[1]);
            double raw = mlp.predict(s).getValues().get(0);
            int bit = raw >= 0.5 ? 1 : 0;  // umbral 0.5
            System.out.printf("Input [%d, %d] -> raw=%.4f -> bit=%d%n",
                              (int)inp[0], (int)inp[1], raw, bit);
        }
    }
}
