package com.merlab.signals;


/**
 * Extrae características de una señal o de sus estadísticas.
 */
public class FeatureExtractor {

    /**
     * Extrae un vector de características a partir de la señal de entrada.
     * Aquí, simplemente devolvemos media y varianza.
     * @param input señal procesada
     * @return señal de características [media, varianza]
     */
    public static Signal extractFeatures(Signal input) {
        // Por defecto, devolvemos las estadísticas como “features”
        return StatisticalProcessor.extractStats(input);
    }

    // Puedes añadir más métodos:
    // public static Signal extractSpectrum(Signal input) { … }
    // public static Signal extractPeaks(Signal input) { … }
    // etc.
}
