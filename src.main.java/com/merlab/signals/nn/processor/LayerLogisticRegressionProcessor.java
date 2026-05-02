package com.merlab.signals.nn.processor;

import java.util.Objects;
import java.util.stream.DoubleStream;

import com.merlab.signals.core.Signal;

/**
 * Logistic regression processor backed by a single Layer.
 *
 * This version is kept for experiments and examples that train logistic
 * regression through the generic Layer abstraction. The direct active version
 * is LogisticRegressionProcessor, which stores weights and bias directly.
 */
public class LayerLogisticRegressionProcessor implements NeuralNetworkProcessor {
    private final Layer layer;

    public LayerLogisticRegressionProcessor(Layer layer) {
        this.layer = Objects.requireNonNull(layer);
    }

    public Layer getLayer() {
        return layer;
    }

    public LayerLogisticRegressionProcessor copy() {
        double[][] originalWeights = layer.getWeights();
        int neurons = originalWeights.length;
        int inputs = originalWeights[0].length;

        double[][] copiedWeights = new double[neurons][inputs];
        for (int i = 0; i < neurons; i++) {
            System.arraycopy(originalWeights[i], 0, copiedWeights[i], 0, inputs);
        }

        double[] originalBiases = layer.getBiases();
        double[] copiedBiases = new double[neurons];
        System.arraycopy(originalBiases, 0, copiedBiases, 0, neurons);

        ActivationFunction activation = layer.getActivationFunction();
        Layer layerCopy = new Layer(copiedWeights, copiedBiases, activation);
        return new LayerLogisticRegressionProcessor(layerCopy);
    }

    @Override
    public Signal predict(Signal features) {
        double[] x = features.getValues().stream().flatMapToDouble(DoubleStream::of).toArray();
        double[] z = layer.forward(x);
        Signal out = new Signal();
        out.add(z[0]);
        return out;
    }
}
