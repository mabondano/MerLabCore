package com.merlab.signals.features;

import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

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
    
    // En com.merlab.signals.features.FeatureExtractor
    public static Signal extractFeaturesMean(Signal input) {
        double mean = StatisticalProcessor.mean(input);
        Signal features = new Signal();
        features.add(mean);
        return features;
    }


    // Puedes añadir más métodos:
    // public static Signal extractSpectrum(Signal input) { … }
    // public static Signal extractPeaks(Signal input) { … }
    // etc.
    
    public static Signal extractFeatures2(Signal input) {
        List<Double> feats = new ArrayList<>();
        feats.addAll(StatisticalProcessor.extractStats(input).getValues());
        //feats.add(computeRMS(input));
        //feats.add(computePeak(input));
        // …
        return new Signal(feats);
    }

}
