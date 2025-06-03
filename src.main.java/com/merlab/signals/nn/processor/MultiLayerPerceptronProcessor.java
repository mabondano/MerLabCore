// src/main/java/com/merlab/signals/nn/processor/MultiLayerPerceptronProcessor.java
package com.merlab.signals.nn.processor;

import com.merlab.signals.core.Signal;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * Procesador de inferencia para un perceptrón multicapa (MLP) simple.
 */
public class MultiLayerPerceptronProcessor implements NeuralNetworkProcessor {

    private final List<Layer> layers;
    private Mode mode = Mode.TRAIN;         // ← campo nuevo

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
    	setMode(Mode.INFERENCE);           // ← forzamos INFERENCE en predict
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
    
    public void setMode2(Mode m) {          // ← método setter
        this.mode = m;
    }
    
    public void setMode(Mode m) {
        this.mode = m;
        layers.forEach(l -> {
            if (l instanceof BatchNormLayer) ((BatchNormLayer) l).setMode(m);
            if (l instanceof DropoutLayer)  ((DropoutLayer) l).setMode(m);
        });
    }

    
    /** Devuelve la lista de capas (para backprop). */
    public List<Layer> getLayers() {
        return layers;
    }

    /** Clona capas y pesos para no modificar el original. */
    public MultiLayerPerceptronProcessor copy() {
        List<Layer> copyLayers = layers.stream()
            .map(layer -> {
                // copia profunda de pesos y biases
                double[][] w = new double[layer.getNeurons()][layer.getInputsPerNeuron()];
                for (int i = 0; i < w.length; i++)
                    System.arraycopy(layer.getWeights()[i], 0, w[i], 0, w[i].length);
                double[] b = new double[layer.getNeurons()];
                System.arraycopy(layer.getBiases(), 0, b, 0, b.length);
                return new Layer(w, b, layer.getActivation());
            })
            .collect(Collectors.toList());
        return new MultiLayerPerceptronProcessor(copyLayers);
    }

}
