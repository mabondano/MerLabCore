package com.merlab.signals.experimental.nn.processor;

import java.util.Objects;
import java.util.stream.DoubleStream;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

/**
 * Basic historical layer-based logistic regression alternative.
 *
 * The active layer-based implementation is
 * com.merlab.signals.nn.processor.LayerLogisticRegressionProcessor, which also
 * exposes getLayer() and copy() for training.
 */
public class BasicLayerLogisticRegressionAlternative implements NeuralNetworkProcessor {
    private final Layer layer;

    public BasicLayerLogisticRegressionAlternative(Layer layer) {
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
