package com.merlab.signals.nn.processor;

//Sigmoid
public class SigmoidActivation implements ActivationFunction {
    @Override
    public double apply(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
}

