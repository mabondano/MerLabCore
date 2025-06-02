package com.merlab.signals.nn.processor;

//ReLU
public class ReLUActivation implements ActivationFunction {
    @Override
    public double apply(double x) {
        return Math.max(0.0, x);
    }
}