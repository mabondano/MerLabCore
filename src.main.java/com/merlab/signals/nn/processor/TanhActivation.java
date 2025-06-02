package com.merlab.signals.nn.processor;

//Tanh
public class TanhActivation implements ActivationFunction {
    @Override
    public double apply(double x) {
        return Math.tanh(x);
    }
}
