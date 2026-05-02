package com.merlab.signals.experimental.nn.processor;

import com.merlab.signals.nn.processor.ActivationFunction;

/**
 * Basic historical identity activation kept for comparison.
 *
 * The active implementation is ActivationFunctions.IDENTITY.
 */
public class BasicIdentityActivationAlternative implements ActivationFunction {
    @Override
    public double apply(double x) {
        return x;
    }

    @Override
    public double derivative(double x) {
        return 1.0;
    }
}
