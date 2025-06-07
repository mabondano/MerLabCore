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
import java.util.Random;

import org.apache.commons.math3.distribution.*;
import org.apache.commons.math3.random.JDKRandomGenerator;

/**
 * Generador de señales basadas en distribuciones estadísticas.
 * Implementa SignalProvider para integrarse con el pipeline.
 */
public class DistributionGenerator extends AbstractSignalGenerator {

    public enum DistType {
        NORMAL,         // parámetros: mean, stddev
        UNIFORM,        // parámetros: min, max
        TRIANGULAR,     // parámetros: lower, mode, upper
        BINOMIAL,       // parámetros: trials, p
        POISSON,        // parámetro: mean
        EXPONENTIAL,    // parámetro: mean
        GEOMETRIC,      // parámetro: p
        WEIBULL,        // parámetros: shape, scale
        BETA,           // parámetros: α, β
        LOGNORMAL,      // parámetros: scale, shape
        HYPERGEOMETRIC, // parámetros: populationSize, numberOfSuccesses
        CHI_SQUARE,     // parámetro: degreesOfFreedom
        STUDENT_T,      // parámetro: degreesOfFreedom
        GAMMA           // parámetros: shape, scale
        // … luego el resto de los 86 …
    }

    private final DistType type;
    private final double param1;
    private final double param2;
    private final Random rng;

    /** Constructor completo */
    public DistributionGenerator(DistType type,
                                 int size,
                                 double p1,
                                 double p2,
                                 Long seed) {
        super(size, seed);
        this.type   = type;
        this.param1 = p1;
        this.param2 = p2;
        this.rng    = new JDKRandomGenerator();
        if (seed != null) {
            this.rng.setSeed(seed);
        }
    }

    /** Constructor sin semilla */
    public DistributionGenerator(DistType type,
                                 int size,
                                 double p1,
                                 double p2) {
        this(type, size, p1, p2, null);
    }

    @Override
    public Signal getSignal() {
        List<Double> values = new ArrayList<>(size);

        switch (type) {
            case NORMAL: {
                // Usamos el constructor sin rng para simplificar
                NormalDistribution d = new NormalDistribution(param1, param2);
                for (int i = 0; i < size; i++) {
                    values.add(d.sample());
                }
                break;
            }
            case UNIFORM: {
                UniformRealDistribution d = new UniformRealDistribution(param1, param2);
                for (int i = 0; i < size; i++) {
                    values.add(d.sample());
                }
                break;
            }
            case TRIANGULAR: {
                TriangularDistribution d = new TriangularDistribution(
                    param1,           // lower
                    (param1+param2)/2,// mode
                    param2            // upper
                );
                for (int i = 0; i < size; i++) {
                    values.add(d.sample());
                }
                break;
            }
            case BINOMIAL: {
                BinomialDistribution d = new BinomialDistribution(
                    (int)param1,     // trials
                    param2           // p
                );
                for (int i = 0; i < size; i++) {
                    values.add((double)d.sample());
                }
                break;
            }
            case POISSON: {
                PoissonDistribution d = new PoissonDistribution(
                    param1,                             // mean
                    PoissonDistribution.DEFAULT_EPSILON,
                    PoissonDistribution.DEFAULT_MAX_ITERATIONS
                );
                for (int i = 0; i < size; i++) {
                    values.add((double)d.sample());
                }
                break;
            }
            case EXPONENTIAL: {
                ExponentialDistribution d = new ExponentialDistribution(param1);
                for (int i = 0; i < size; i++) {
                    values.add(d.sample());
                }
                break;
            }
            case GEOMETRIC: {
                GeometricDistribution d = new GeometricDistribution(param1);
                for (int i = 0; i < size; i++) {
                    values.add((double)d.sample());
                }
                break;
            }
            case WEIBULL: {
                WeibullDistribution d = new WeibullDistribution(param1, param2);
                for (int i = 0; i < size; i++) {
                    values.add(d.sample());
                }
                break;
            }
            case BETA: {
                BetaDistribution d = new BetaDistribution(param1, param2);
                for (int i = 0; i < size; i++) {
                    values.add(d.sample());
                }
                break;
            }
            case LOGNORMAL: {
                LogNormalDistribution d = new LogNormalDistribution(param1, param2);
                for (int i = 0; i < size; i++) {
                    values.add(d.sample());
                }
                break;
            }
            case HYPERGEOMETRIC: {
                HypergeometricDistribution d = new HypergeometricDistribution(
                    (int)param1,  // populationSize
                    (int)param2,  // numberOfSuccesses
                    size          // sampleSize
                );
                for (int i = 0; i < size; i++) {
                    values.add((double)d.sample());
                }
                break;
            }
            case CHI_SQUARE: {
                ChiSquaredDistribution d = new ChiSquaredDistribution(param1);
                for (int i = 0; i < size; i++) {
                    values.add(d.sample());
                }
                break;
            }
            case STUDENT_T: {
                TDistribution d = new TDistribution(param1);
                for (int i = 0; i < size; i++) {
                    values.add(d.sample());
                }
                break;
            }
            case GAMMA: {
                GammaDistribution d = new GammaDistribution(param1, param2);
                for (int i = 0; i < size; i++) {
                    values.add(d.sample());
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Distribución no soportada: " + type);
        }

        return new Signal(values);
    }

}

