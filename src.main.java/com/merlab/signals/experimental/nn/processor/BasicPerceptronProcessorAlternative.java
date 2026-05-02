package com.merlab.signals.experimental.nn.processor;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

/**
 * Basic historical perceptron processor with a fixed sigmoid activation.
 *
 * The active implementation is ConfigPerceptronProcessor, which accepts any
 * ActivationFunction.
 */
public class BasicPerceptronProcessorAlternative implements NeuralNetworkProcessor {

    private final double[] weights;
    private final double bias;

    public BasicPerceptronProcessorAlternative(double[] weights, double bias) {
        this.weights = weights;
        this.bias = bias;
    }

    @Override
    public Signal predict(Signal features) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * features.getValues().get(i);
        }
        double activated = 1.0 / (1.0 + Math.exp(-sum));
        Signal output = new Signal();
        output.add(activated);
        return output;
    }
}
