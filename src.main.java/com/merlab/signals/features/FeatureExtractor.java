/*
 * Copyright 2025 Merly Abondano
 *
 * Created:   2025-06-07
 * Author:    Merly Abondano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
