package com.merlab.signals.nn.processor;

//ReLU
public class ReLUActivation implements ActivationFunction {
  @Override
  public double apply(double x) {
      return Math.max(0.0, x);
  }
  @Override public double derivative(double x) {
      return x > 0 ? 1.0 : 0.0;
  }
}
/*
public class ReLUActivation implements ActivationFunction {
    @Override
    public double apply(double x) {
        return Math.max(0.0, x);
    }
}
*/