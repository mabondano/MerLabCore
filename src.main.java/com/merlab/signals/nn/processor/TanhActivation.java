package com.merlab.signals.nn.processor;

//Tanh
public class TanhActivation implements ActivationFunction {
  @Override
  public double apply(double x) {
      return Math.tanh(x);
  }
  @Override public double derivative(double x) {
      double t = apply(x);
      return 1 - t * t;
  }
}
/*
public class TanhActivation implements ActivationFunction {
    @Override
    public double apply(double x) {
        return Math.tanh(x);
    }
}
*/
