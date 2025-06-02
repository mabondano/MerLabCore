package com.merlab.signals.nn.processor;

//Identity (lineal)
public class IdentityActivation implements ActivationFunction {
    @Override
    public double apply(double x) {
        return x;
    }
}