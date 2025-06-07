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

package com.merlab.signals.core;

import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.core.SignalGenerator.Type;

/**
 * Generadores de formas de onda (seno, cuadrada, triangular…).
 */
public class CustomSignalGenerator extends AbstractSignalGenerator {
    public enum DistType {
        SINE,
        NORMAL,
        UNIFORM
    }
    
    private final DistType type;
    private final double amplitude, frequency, phase;
    private final double param1;
    private final double param2;
    // …otros parámetros…

    public CustomSignalGenerator(DistType type,
                                 int size,
                                 double amplitude,
                                 double frequency,
                                 double phase,
                                 double p1,
                                 double p2,
                                 Long seed) {
        super(size, seed);
        this.type      = type;
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.phase     = phase;
        this.param1 = p1;
        this.param2 = p2;
    }

    @Override
    public Signal getSignal() {
        List<Double> values = new ArrayList<>(size);
        switch (type) {
            case SINE:
                for (int i = 0; i < size; i++) {
                	values.add(amplitude * Math.sin(2*Math.PI*frequency*i/size + phase));
                }
                break;
            case NORMAL:
                double mean   = param1;
                double stddev = param2;
                for (int i = 0; i < size; i++) {
                    values.add(mean + stddev * rng.nextGaussian());
                }
                break;

            case UNIFORM:
                double min = param1;
                double max = param2;
                for (int i = 0; i < size; i++) {
                    values.add(min + rng.nextDouble() * (max - min));
                }
                break;
                
            default:
            throw new UnsupportedOperationException("Distribución no soportada: " + type);    
            // …otros casos…
        }
        return new Signal(values);
    }
}


