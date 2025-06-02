package com.merlab.signals.nn.processor;

import com.merlab.signals.core.Signal;

/**
 * Placeholder de procesador de red neuronal.
 * Actualmente aplica una transformación simple (escala de 0.5) a la señal.
 * Más adelante se integrará un modelo real de inferencia.
 */
/**
 * Implementación placeholder de NeuralNetworkProcessor:
 * simplemente escala cada valor por 0.5.
 */
public class DefaultNeuralNetworkProcessor implements NeuralNetworkProcessor {

    @Override
    public Signal predict(Signal features) {
        Signal output = new Signal();
        for (Double v : features.getValues()) {
            output.add(v * 0.5);
        }
        return output;
    }
    
    public static Signal predict2(Signal features) {
        Signal output = new Signal();
        for (Double v : features.getValues()) {
            output.add(v * 0.5);
        }
        return output;
    }
}
