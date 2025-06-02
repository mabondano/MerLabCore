// src/main/java/com/merlab/signals/nn/model/RegressionModel.java
package com.merlab.signals.nn.model;

/**
 * Contiene los parámetros de una regresión lineal múltiple.
 */
public class RegressionModel {
    private final double[] weights;
    private final double bias;

    public RegressionModel(double[] weights, double bias) {
        this.weights = weights.clone();
        this.bias    = bias;
    }

    public double[] getWeights() { return weights.clone(); }
    public double   getBias()    { return bias; }
}
