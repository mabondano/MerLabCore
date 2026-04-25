package com.merlab.signals.nn.processor;

import java.util.Collections;

import com.merlab.signals.core.Signal;

public class SimplePerceptronProcessor implements NeuralNetworkProcessor {
	
    private final double[] weights;
    private final double bias;
    
    public SimplePerceptronProcessor(double[] weights, double bias) {
        this.weights = weights;
        this.bias = bias;
    }
    
    @Override
    public Signal predict(Signal features) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * features.getValues().get(i);
        }
        double activated = 1.0 / (1.0 + Math.exp(-sum)); // sigmoid
        Signal output = new Signal();
        output.add(activated);
        return output;
    }
    
  
    public Signal predict2(Signal features) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * features.getValues().get(i);
        }
        return new Signal(Collections.singletonList(activation(sum)));
    }
    private double activation(double x) { return 1/(1+Math.exp(-x)); }
}
