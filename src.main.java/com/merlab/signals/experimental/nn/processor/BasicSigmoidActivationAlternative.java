package com.merlab.signals.experimental.nn.processor;

import com.merlab.signals.nn.processor.ActivationFunction;

/**
 * Basic historical sigmoid activation kept for comparison.
 *
 * The active implementation is ActivationFunctions.SIGMOID.
 */
public class BasicSigmoidActivationAlternative implements ActivationFunction {
    @Override
    public double apply(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    @Override
    public double derivative(double x) {
        double s = apply(x);
        return s * (1 - s);
    }
}
