// src/main/java/com/merlab/signals/nn/processor/Layer.java
package com.merlab.signals.nn.processor;

import java.util.Objects;

/**
 * Capa de una red neuronal: realiza la operación lineal + activación.
 */
public class Layer {

    private final double[][] weights;       // [nOutputs][nInputs]
    private final double[]   biases;        // [nOutputs]
    private final ActivationFunction activation;

    /**
     * @param weights    matriz de pesos de tamaño [nOutputs][nInputs]
     * @param biases     vector de sesgos de longitud nOutputs
     * @param activation función de activación para cada neurona
     */
    public Layer(double[][] weights, double[] biases, ActivationFunction activation) {
        this.weights    = Objects.requireNonNull(weights,    "weights");
        this.biases     = Objects.requireNonNull(biases,     "biases");
        this.activation = Objects.requireNonNull(activation, "activation");
        if (weights.length != biases.length) {
            throw new IllegalArgumentException(
                "El número de filas en weights debe coincidir con biases.length");
        }
    }

    /**
     * Propaga la entrada a través de esta capa.
     *
     * @param inputs vector de entrada de longitud igual al número de columnas de weights
     * @return vector de salida de longitud biases.length
     */
    public double[] forward(double[] inputs) {
        if (inputs.length != weights[0].length) {
            throw new IllegalArgumentException(
                "Longitud de inputs incorrecta: esperado " + weights[0].length
                + " pero fue " + inputs.length);
        }
        double[] outputs = new double[biases.length];
        for (int i = 0; i < biases.length; i++) {
            double sum = biases[i];
            for (int j = 0; j < inputs.length; j++) {
                sum += weights[i][j] * inputs[j];
            }
            outputs[i] = activation.apply(sum);
        }
        return outputs;
    }
}
