package com.merlab.signals;

/**
 * Placeholder de procesador de red neuronal.
 * Actualmente aplica una transformación simple (escala de 0.5) a la señal.
 * Más adelante se integrará un modelo real de inferencia.
 */
public class NeuralNetworkProcessor {

    /**
     * Toma una señal de características y devuelve una nueva señal con el resultado de la "inferencia".
     * @param features señal de entrada (features)
     * @return señal de salida (predicción placeholder)
     */
    public static Signal predict(Signal features) {
        // Placeholder: escala cada valor en un 0.5
        Signal output = new Signal();
        for (Double v : features.getValues()) {
            output.add(v * 0.5);
        }
        return output;
    }
}
