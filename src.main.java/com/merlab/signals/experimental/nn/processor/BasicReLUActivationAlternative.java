package com.merlab.signals.experimental.nn.processor;

import com.merlab.signals.nn.processor.ActivationFunction;

/**
 * Basic historical ReLU activation kept for comparison.
 *
 * The active implementation is ActivationFunctions.RELU.
 */
public class BasicReLUActivationAlternative implements ActivationFunction {
    @Override
    public double apply(double x) {
        return Math.max(0.0, x);
    }

    @Override
    public double derivative(double x) {
        return x > 0 ? 1.0 : 0.0;
    }
}
