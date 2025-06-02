// src/main/java/com/merlab/signals/nn/processor/MultiLayerPerceptronProcessor.java
package com.merlab.signals.nn.processor;

import com.merlab.signals.core.Signal;

import java.util.List;
import java.util.Objects;
import java.util.stream.DoubleStream;

/**
 * Procesador de inferencia para un perceptrón multicapa (MLP) simple.
 */
public class MultiLayerPerceptronProcessor implements NeuralNetworkProcessor {

    private final List<Layer> layers;

    /**
     * @param layers lista de capas en orden: de entrada hacia la salida
     */
    public MultiLayerPerceptronProcessor(List<Layer> layers) {
        this.layers = Objects.requireNonNull(layers, "layers");
        if (layers.isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos una capa");
        }
    }

    @Override
    public Signal predict(Signal features) {
        // Convierte la señal en array de double
        double[] vals = features.getValues()
                                .stream()
                                .flatMapToDouble(DoubleStream::of)
                                .toArray();
        // Propaga por cada capa
        for (Layer layer : layers) {
            vals = layer.forward(vals);
        }
        // Reconstruye el Signal de salida
        Signal out = new Signal();
        for (double v : vals) {
            out.add(v);
        }
        return out;
    }
}
