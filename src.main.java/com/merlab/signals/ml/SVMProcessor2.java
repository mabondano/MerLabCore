package com.merlab.signals.ml;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import java.util.ArrayList;
import java.util.List;

public class SVMProcessor2 implements NeuralNetworkProcessor {
    private double[] weights;  // w vector
    private double bias;       // b

    public SVMProcessor2(int inputDim) {
        this.weights = new double[inputDim];
        this.bias = 0.0;
    }

    // Setters for manual testing
    public void setWeights(double[] weights) {
        if (weights.length != this.weights.length)
            throw new IllegalArgumentException("Weights dimension mismatch.");
        System.arraycopy(weights, 0, this.weights, 0, weights.length);
    }
    public void setBias(double bias) {
        this.bias = bias;
    }

    // Utilidad: convierte List<Double> a double[]
    private double[] toPrimitiveArray(List<Double> list) {
        double[] arr = new double[list.size()];
        for (int i = 0; i < list.size(); i++)
            arr[i] = list.get(i);
        return arr;
    }

    @Override
    public Signal predict(Signal input) {
        List<Double> xList = input.getValues();
        double[] x = toPrimitiveArray(xList);
        double dot = 0.0;
        for (int i = 0; i < weights.length; i++)
            dot += weights[i] * x[i];
        double result = dot + bias;
        // Regresa un Signal de un solo valor (+1.0 o -1.0) como List<Double>
        List<Double> output = new ArrayList<>();
        output.add(result >= 0 ? 1.0 : -1.0);
        return new Signal(output);
    }

    // (Opcional) Predicción por lote
    public List<Signal> predict(List<Signal> inputs) {
        List<Signal> out = new ArrayList<>();
        for (Signal s : inputs) out.add(predict(s));
        return out;
    }

    // Getters
    public double[] getWeights() {
        return weights;
    }

    public double getBias() {
        return bias;
    }


}