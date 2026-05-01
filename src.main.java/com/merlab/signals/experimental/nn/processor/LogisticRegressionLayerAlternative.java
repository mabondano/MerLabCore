package com.merlab.signals.experimental.nn.processor;

import java.util.Objects;
import java.util.stream.DoubleStream;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

/**
 * Alternative logistic regression design kept for comparison.
 * This version delegates prediction to a single Layer.
 */
public class LogisticRegressionLayerAlternative implements NeuralNetworkProcessor {
    private final Layer layer;

    public LogisticRegressionLayerAlternative(Layer layer) {
        this.layer = Objects.requireNonNull(layer);
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
