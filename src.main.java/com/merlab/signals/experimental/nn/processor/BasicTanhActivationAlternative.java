package com.merlab.signals.experimental.nn.processor;

import com.merlab.signals.nn.processor.ActivationFunction;

/**
 * Basic historical tanh activation kept for comparison.
 *
 * The active implementation is ActivationFunctions.TANH.
 */
public class BasicTanhActivationAlternative implements ActivationFunction {
    @Override
    public double apply(double x) {
        return Math.tanh(x);
    }

    @Override
    public double derivative(double x) {
        double t = apply(x);
        return 1 - t * t;
    }
}
