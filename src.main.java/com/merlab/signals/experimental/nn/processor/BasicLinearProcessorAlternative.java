package com.merlab.signals.experimental.nn.processor;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

/**
 * Basic historical linear processor kept for comparison.
 *
 * The active library version is com.merlab.signals.nn.processor.LinearProcessor.
 * This alternative preserves the original simple implementation used while
 * the linear regression examples were being developed.
 */
public class BasicLinearProcessorAlternative implements NeuralNetworkProcessor {
    private final double[] weights;
    private final double bias;

    public BasicLinearProcessorAlternative(double[] weights, double bias) {
        this.weights = weights;
        this.bias = bias;
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
