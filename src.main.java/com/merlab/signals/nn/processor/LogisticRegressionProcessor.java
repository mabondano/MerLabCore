package com.merlab.signals.nn.processor;

import com.merlab.signals.core.Signal;

/**
 * Logistic regression processor: sigmoid(w*x + b).
 */
public class LogisticRegressionProcessor implements NeuralNetworkProcessor {
    private double[] weights;
    private double bias;

    public LogisticRegressionProcessor(double[] weights, double bias) {
        this.weights = weights.clone();
        this.bias = bias;
    }

    @Override
    public Signal predict(Signal features) {
        double[] x = features.getValues().stream().mapToDouble(d -> d).toArray();
        double z = bias;
        for (int i = 0; i < weights.length; i++) {
            z += weights[i] * x[i];
        }
        double p = 1.0 / (1.0 + Math.exp(-z));
        Signal out = new Signal();
        out.add(p);
        return out;
    }

    public LogisticRegressionProcessor copy() {
        return new LogisticRegressionProcessor(this.weights, this.bias);
    }

    public double[] getWeights() {
        return weights;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }
}
