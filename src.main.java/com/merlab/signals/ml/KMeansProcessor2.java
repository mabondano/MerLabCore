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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.merlab.signals.core.Signal;

/**
 * K-Means simple sobre señales. Tras llamar a fit(), se pueden obtener
 * los centroides finales y las etiquetas (cluster index) por punto.
 */
public class KMeansProcessor2 {

    private final int k;
    private final int maxIters;
    private final Random rnd;

    // Cada centroide es un arreglo de double (misma dimensión que cada Signal)
    private List<double[]> centroids;

    // Para devolver luego las etiquetas finales
    private int[] labels;

    public KMeansProcessor2(int k, int maxIters, long seed) {
        if (k <= 0) {
            throw new IllegalArgumentException("k debe ser > 0");
        }
        this.k = k;
        this.maxIters = maxIters;
        this.rnd = new Random(seed);
    }

    /**
     * Entrena K-means sobre la lista de Signals (solo inputs).
     * No modifica la lista original de inputs. Al terminar, guarda en
     * `centroids` los k centros finales, y en `labels` la etiqueta de cluster
     * para cada índice i en la lista original de inputs.
     *
     * @param inputs lista original de Signals (cada uno de dimensión D)
     */
    public void fit(List<Signal> inputs) {
        int nSamples = inputs.size();
        if (nSamples == 0) {
            throw new IllegalArgumentException("La lista de inputs no puede estar vacía");
        }
        int dim = inputs.get(0).getValues().size();

        // 1) Construir copia de inputs para no mutar la lista original
        List<Signal> data = new ArrayList<>(inputs);

        // 2) Elegir k centroides iniciales aleatoriamente (de la copia)
        centroids = new ArrayList<>(k);
        Collections.shuffle(data, rnd);
        for (int i = 0; i < k; i++) {
            double[] init = data.get(i).getValues().stream().mapToDouble(d -> d).toArray();
            centroids.add(init.clone());
        }

        // 3) Iterar reclasificación ↔ recalcular centroides
        for (int iter = 0; iter < maxIters; iter++) {
            // Preparar k clusters vacíos
            @SuppressWarnings("unchecked")
            List<List<Signal>> clusters = new ArrayList<>(k);
            for (int i = 0; i < k; i++) {
                clusters.add(new ArrayList<>());
            }

            // Asignar cada punto de 'data' al centroide más cercano
            for (Signal s : data) {
                double[] x = s.getValues().stream().mapToDouble(d -> d).toArray();
                int bestIdx = 0;
                double bestDist = euclidSquared(x, centroids.get(0));
                for (int c = 1; c < k; c++) {
                    double d2 = euclidSquared(x, centroids.get(c));
                    if (d2 < bestDist) {
                        bestDist = d2;
                        bestIdx = c;
                    }
                }
                clusters.get(bestIdx).add(s);
            }

            // Recalcular cada centroide como media de su cluster
            boolean converged = true;
            for (int c = 0; c < k; c++) {
                List<Signal> clusterPoints = clusters.get(c);
                if (clusterPoints.isEmpty()) {
                    // Si un cluster quedó vacío, dejamos el centroide anterior
                    continue;
                }
                double[] newCentroid = new double[dim];
                // Sumar todas las coordenadas
                for (Signal s : clusterPoints) {
                    for (int d = 0; d < dim; d++) {
                        newCentroid[d] += s.getValues().get(d);
                    }
                }
                // Dividir por el tamaño del cluster
                for (int d = 0; d < dim; d++) {
                    newCentroid[d] /= clusterPoints.size();
                }
                // Verificar convergencia aproximada
                if (!almostEqual(newCentroid, centroids.get(c))) {
                    converged = false;
                }
                centroids.set(c, newCentroid);
            }
            if (converged) {
                break;
            }
        }

        // 4) Una vez que tenemos centroides finales, asignar etiquetas a cada punto original
        labels = new int[nSamples];
        for (int i = 0; i < nSamples; i++) {
            double[] x = inputs.get(i).getValues().stream().mapToDouble(d -> d).toArray();
            int bestIdx = 0;
            double bestDist = euclidSquared(x, centroids.get(0));
            for (int c = 1; c < k; c++) {
                double d2 = euclidSquared(x, centroids.get(c));
                if (d2 < bestDist) {
                    bestDist = d2;
                    bestIdx = c;
                }
            }
            labels[i] = bestIdx;
        }
    }

    /**
     * Devuelve el arreglo de etiquetas (0..k-1) asignadas a cada punto en el
     * mismo orden en que fueron pasados a fit().
     */
    public int[] getLabels() {
        if (labels == null) {
            throw new IllegalStateException("Debe llamar a fit(...) antes de getLabels()");
        }
        return labels.clone();
    }

    /**
     * Devuelve la lista de centroides finales (cada double[] con longitud = dimensionalidad).
     */
    public List<double[]> getCentroids() {
        if (centroids == null) {
            throw new IllegalStateException("Debe llamar a fit(...) antes de getCentroids()");
        }
        return centroids;
    }

    /** 
     * Euclidiana al cuadrado (para evitar calcular sqrt innecesario). 
     */
    private static double euclidSquared(double[] a, double[] b) {
        double s = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            s += diff * diff;
        }
        return s;
    }

    /**
     * Compara dos vectores a y b de igual longitud: devuelve true si
     * |a[i] - b[i]| ≤ 1e-6 para todo i; false en caso contrario.
     */
    private static boolean almostEqual(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i] - b[i]) > 1e-6) {
                return false;
            }
        }
        return true;
    }
}
