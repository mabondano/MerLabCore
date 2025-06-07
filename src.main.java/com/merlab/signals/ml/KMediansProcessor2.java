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
import java.util.Comparator;
import java.util.List;

/**
 * KMediansProcessor2: implementa K-Medians en lugar de K-Means.
 * - Cada centroide se actualiza tomando la mediana de cada dimensión
 *   (en lugar de la media).
 * - El resto de la estructura es idéntica a KMeansProcessor2.
 */
public class KMediansProcessor2 {

    private final List<Signal> trainInputs;
    private final int k;
    private final int maxIterations;

    private final List<double[]> centroids; // cada centroide es un array de double[]

    /**
     * Constructor de “entrenamiento” de K-Medians:
     *   1) Inicializa los k centroides (aleatoriamente tomando k puntos del DataSet).
     *   2) Guarda datos y parámetros.
     *
     * @param ds             DataSet de entrenamiento (cada target puede ignorarse aquí).
     * @param k              número de clusters (impar o par, no importa).
     * @param maxIterations  número máximo de iteraciones (para detener si no converge).
     * @param rnd            generador de números aleatorios (para elección inicial de centroides).
     */
    public KMediansProcessor2(DataSet ds, int k, int maxIterations, java.util.Random rnd) {
        if (k <= 0) {
            throw new IllegalArgumentException("K debe ser > 0");
        }
        this.trainInputs    = new ArrayList<>(ds.getInputs());
        this.k              = k;
        this.maxIterations  = maxIterations;
        this.centroids      = new ArrayList<>(k);

        // 1) Inicializar centroides eligiendo k puntos aleatorios distintos (sin reemplazo).
        List<Signal> copyInputs = new ArrayList<>(ds.getInputs());
        Collections.shuffle(copyInputs, rnd);
        for (int i = 0; i < k; i++) {
            double[] coords = copyInputs.get(i).getValues().stream().mapToDouble(d -> d).toArray();
            // hacemos una copia defensiva
            centroids.add(coords.clone());
        }
    }

    /**
     * Ejecuta el algoritmo de K-Medians sobre los datos de entrenamiento
     * y devuelve la asignación de cluster para cada punto (lista de índices de 0..k-1).
     *
     * @return un array de tamaño N (N = número de puntos), donde result[i] = índice del cluster de la i-ésima señal.
     */
    public int[] fit() {
        int nPoints = trainInputs.size();
        int dim = trainInputs.get(0).getValues().size();

        // Asignación actual de clusters (por cada punto, guarda el índice de centroide)
        int[] labels = new int[nPoints];

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            boolean changed = false;

            // 1) **Asignar cada punto al centroide más cercano (por distancia euclidiana)** 
            for (int i = 0; i < nPoints; i++) {
                double[] x = trainInputs.get(i).getValues().stream().mapToDouble(d -> d).toArray();
                int bestCluster = -1;
                double bestDistSquared = Double.POSITIVE_INFINITY;
                for (int c = 0; c < k; c++) {
                    double[] centroid = centroids.get(c);
                    // distancia Euclidiana (sin raíz) solo para comparación
                    double dist2 = 0.0;
                    for (int d = 0; d < dim; d++) {
                        double diff = x[d] - centroid[d];
                        dist2 += diff * diff;
                    }
                    if (dist2 < bestDistSquared) {
                        bestDistSquared = dist2;
                        bestCluster = c;
                    }
                }
                if (labels[i] != bestCluster) {
                    changed = true;
                    labels[i] = bestCluster;
                }
            }

            // Si en esta iteración NO cambió ninguna asignación, podemos detener
            if (!changed && iteration > 0) {
                break;
            }

            // 2) Recalcular cada centroide como la **mediana** por cada dimensión
            //    Creamos k listas de “puntos de cada cluster”
            List<List<double[]>> clusters = new ArrayList<>(k);
            for (int c = 0; c < k; c++) {
                clusters.add(new ArrayList<>());
            }
            for (int i = 0; i < nPoints; i++) {
                clusters.get(labels[i]).add(
                    trainInputs.get(i).getValues().stream().mapToDouble(d -> d).toArray()
                );
            }

            // Para cada cluster, calcular la mediana en cada dimensión
            for (int c = 0; c < k; c++) {
                List<double[]> clusterPoints = clusters.get(c);
                if (clusterPoints.isEmpty()) {
                    // si no hay puntos asignados, dejamos el centroide anterior
                    continue;
                }
                double[] newCentroid = new double[dim];
                // Para cada dimensión d:
                for (int d = 0; d < dim; d++) {
                    // Extraemos todos los valores de la dimensión d en este cluster
                    double[] vals = new double[clusterPoints.size()];
                    for (int i = 0; i < clusterPoints.size(); i++) {
                        vals[i] = clusterPoints.get(i)[d];
                    }
                    // Ordenamos y tomamos la mediana
                    java.util.Arrays.sort(vals);
                    int m = vals.length;
                    if (m % 2 == 1) {
                        newCentroid[d] = vals[m / 2];
                    } else {
                        // mediana promedio de las dos centrales
                        newCentroid[d] = (vals[m/2 - 1] + vals[m/2]) / 2.0;
                    }
                }
                centroids.set(c, newCentroid);
            }
        }

        return labels;
    }

    /**
     * Devuelve una copia defensiva de los centroides finales (después de fit()).
     */
    public List<double[]> getCentroids() {
        List<double[]> copy = new ArrayList<>(k);
        for (double[] c : centroids) {
            copy.add(c.clone());
        }
        return copy;
    }
}
