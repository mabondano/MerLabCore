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

package com.merlab.signals.ml;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Self‐Organizing Map (Kohonen Map) de tamaño rows × cols, para vectores
 * de dimensión inputDim.  
 * - train(DataSet): entrena con los datos no supervisados (usa solo inputs).  
 * - getWeights(): devuelve la lista de todos los pesos finales (cada uno es un double[inputDim]).  
 *
 * Ejemplo de uso:
 *   SOMProcessor2 som = new SOMProcessor2(10, 10, 2, 1000, 0.1, 5.0, 42L);
 *   som.train(dataSet);
 *   List<double[]> pesos = som.getWeights();
 */
public class SOMProcessor2 {

    private final int rows;
    private final int cols;
    private final int inputDim;
    private final int totalIterations;
    private final double initialLearningRate;
    private final double initialRadius;
    private final Random rnd;

    // Lista de pesos: tamaño = rows*cols, cada elemento es double[inputDim].
    private List<double[]> weights;

    /**
     * Construye un SOM de tamaño rows×cols para vectores de dimensión inputDim.
     *
     * @param rows                número de filas del mapa
     * @param cols                número de columnas del mapa
     * @param inputDim            dimensión de cada vector de entrada
     * @param totalIterations     número total de iteraciones (= épocas)
     * @param initialLearningRate tasa de aprendizaje inicial
     * @param initialRadius       radio inicial de vecindad (en “unidades de grilla”)
     * @param seed                semilla para reproducibilidad
     */
    public SOMProcessor2(int rows, int cols, int inputDim,
                         int totalIterations,
                         double initialLearningRate,
                         double initialRadius,
                         long seed) {
        if (rows <= 0 || cols <= 0 || inputDim <= 0) {
            throw new IllegalArgumentException("rows, cols e inputDim deben ser > 0");
        }
        this.rows = rows;
        this.cols = cols;
        this.inputDim = inputDim;
        this.totalIterations = totalIterations;
        this.initialLearningRate = initialLearningRate;
        this.initialRadius = initialRadius;
        this.rnd = new Random(seed);

        // Inicialmente, asignamos lista de pesos vacía; se llenará en train()
        this.weights = new ArrayList<>(rows * cols);
    }

    /**
     * Entrena el SOM usando solo los inputs del DataSet (datos no supervisados).
     * Tras invocar este método, los pesos “weights” contendrán el vector de pesos
     * final de cada una de las (rows*cols) neuronas.
     *
     * @param ds DataSet completo (solo se usa ds.getInputs())
     */
    public void train(DataSet ds) {
        List<Signal> inputs = ds.getInputs();
        if (inputs.isEmpty()) {
            throw new IllegalStateException("DataSet vacío: no hay inputs para entrenar.");
        }

        // 1) Inicializar pesos de cada neurona a vectores aleatorios pequeños
        // Tomamos aleatoriamente muestras del dataset para inicializar
        initializeRandomWeights(inputs);

        // 2) Entrenamiento por iteraciones (epochs)
        int N = inputs.size();
        int L = rows * cols;
        for (int t = 0; t < totalIterations; t++) {
            // Decaimiento exponencial de learning rate y radius
            double lr = initialLearningRate * Math.exp(- (double) t / totalIterations);
            double radius = initialRadius * Math.exp(- (double) t / (totalIterations / Math.log(initialRadius)));

            // Para each punto (en orden aleatorio)…
            Collections.shuffle(inputs, rnd);
            for (Signal s : inputs) {
                // 2.1) convertir Signal a arreglo
                double[] x = s.getValues().stream().mapToDouble(d -> d).toArray();

                // 2.2) encontrar BMU (Best‐Matching Unit)
                int bmuIndex = findBMU(x);

                // 2.3) actualizar pesas de todos los neuronas dentro del radius
                int bmuRow = bmuIndex / cols;
                int bmuCol = bmuIndex % cols;

                for (int idx = 0; idx < L; idx++) {
                    int neuronRow = idx / cols;
                    int neuronCol = idx % cols;
                    // distancia cuadriculada en la grilla
                    double dGrid2 = (neuronRow - bmuRow) * (neuronRow - bmuRow)
                                  + (neuronCol - bmuCol) * (neuronCol - bmuCol);
                    double radius2 = radius * radius;
                    if (dGrid2 <= radius2) {
                        // influencia gaussiana
                        double influence = Math.exp(- dGrid2 / (2 * radius2));
                        // peso actual
                        double[] w = weights.get(idx);
                        // w_new = w_old + lr * influence * (x - w_old)
                        for (int d = 0; d < inputDim; d++) {
                            w[d] += lr * influence * (x[d] - w[d]);
                        }
                    }
                }
            }
        }
        // Al terminar, `weights` queda con los vectores finales
    }

    /**
     * Devuelve la lista de pesos finales, después de entrenar.
     * Cada elemento del List es un arreglo double[inputDim] que corresponde a una neurona.
     */
    public List<double[]> getWeights() {
        return Collections.unmodifiableList(weights);
    }

    /**
     * Calcula el índice de la neurona cuyo peso es más cercano al vector x (BMU).
     * @param x vector de entrada (length = inputDim)
     * @return índice entero en [0 .. rows*cols-1]
     */
    private int findBMU(double[] x) {
        int bestIdx = 0;
        double bestDist2 = euclidSquared(x, weights.get(0));
        int total = rows * cols;
        for (int i = 1; i < total; i++) {
            double d2 = euclidSquared(x, weights.get(i));
            if (d2 < bestDist2) {
                bestDist2 = d2;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    /**
     * Inicializa `weights` con vectores tomados aleatoriamente de los inputs (primer L muestras).
     */
    private void initializeRandomWeights(List<Signal> inputs) {
        weights.clear();
        List<Signal> copyInputs = new ArrayList<>(inputs);
        Collections.shuffle(copyInputs, rnd);
        int L = rows * cols;
        for (int i = 0; i < L; i++) {
            double[] xi = copyInputs.get(i % copyInputs.size())
                                  .getValues().stream().mapToDouble(d -> d).toArray();
            // clonamos el vector para no modificar el original
            weights.add(xi.clone());
        }
    }

    /** Distancia Euclidiana al cuadrado entre a y b (sin sqrt). */
    private static double euclidSquared(double[] a, double[] b) {
        double s = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            s += diff * diff;
        }
        return s;
    }
}
