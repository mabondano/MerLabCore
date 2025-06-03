package com.merlab.signals.nn.processor;

import com.merlab.signals.core.Signal;

/**
 * Procesador que aplica sólo la suma ponderada + bias,
 * sin función de activación.
 */
public class SimpleLinearProcessor implements NeuralNetworkProcessor {
    private final double[] weights;
    private final double bias;

    public SimpleLinearProcessor(double[] weights, double bias) {
        this.weights = weights;
        this.bias    = bias;
    }

    @Override
    public Signal predict(Signal features) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * features.getValues().get(i);
        }
        Signal output = new Signal();
        output.add(sum);
        return output;
    }
}
