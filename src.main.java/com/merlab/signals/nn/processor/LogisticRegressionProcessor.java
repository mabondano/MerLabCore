package com.merlab.signals.nn.processor;

import com.merlab.signals.core.Signal;


/**
 * Procesador de regresión logística: w·x + b pasada por sigmoide.
 */
public class LogisticRegressionProcessor implements NeuralNetworkProcessor {
    private double[] weights;
    private double   bias;

    public LogisticRegressionProcessor(double[] weights, double bias) {
        this.weights = weights.clone();
        this.bias    = bias;
    }

    @Override
    public Signal predict(Signal features) {
        double[] x = features.getValues().stream().mapToDouble(d->d).toArray();
        double z = bias;
        for (int i = 0; i < weights.length; i++) {
            z += weights[i] * x[i];
        }
        double p = 1.0 / (1.0 + Math.exp(-z));
        Signal out = new Signal();
        out.add(p);
        return out;
    }

    /** Clona este procesador (pesos y bias). */
    public LogisticRegressionProcessor copy() {
        return new LogisticRegressionProcessor(this.weights, this.bias);
    }

    public double[] getWeights() { return weights; }
    public double   getBias()    { return bias; }
    public void     setBias(double b) { this.bias = b; }
}



/**
 * Regresión logística simple: una capa con activación sigmoide.
 */
/*
public class LogisticRegressionProcessor implements NeuralNetworkProcessor {
    private final Layer layer;

    public LogisticRegressionProcessor(Layer layer) {
        this.layer = Objects.requireNonNull(layer);
    }

    /**
     * @param features Signal con N features
     * @return Signal con un único valor en [0,1]: probabilidad de clase “1”
     *
    @Override
    public Signal predict(Signal features) {
        // Convertir Signal a array
        double[] x = features.getValues().stream().flatMapToDouble(DoubleStream::of).toArray();
        // Forward por la capa
        double[] z = layer.forward(x);
        // Devuelve probabilidad
        Signal out = new Signal();
        out.add(z[0]);  
        return out;
    }
    
}*/
