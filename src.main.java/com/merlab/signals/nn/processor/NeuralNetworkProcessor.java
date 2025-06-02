package com.merlab.signals.nn.processor;

import com.merlab.signals.core.Signal;

/**
 * Interfaz para cualquier procesador de inferencia de red neuronal.
 */
public interface NeuralNetworkProcessor {
    /**
     * Realiza la inferencia sobre los features y devuelve la señal de salida.
     */
    Signal predict(Signal features);
}