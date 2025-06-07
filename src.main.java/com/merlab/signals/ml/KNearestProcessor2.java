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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.distance.DistanceMetrics;
import com.merlab.signals.nn.distance.DistanceMetrics.Metric;

/**
 * K-Nearest Neighbors “versión 2”:
 * - Almacena internamente un DataSet de entrenamiento (inputs + targets).
 * - Permite elegir la métrica de distancia mediante un enum DistanceMetrics.Metric.
 * - Opcionalmente, recibe parámetros en el constructor para métricas que los requieran.
 */
public class KNearestProcessor2 {

    private final List<Signal> trainInputs;
    private final List<Signal> trainTargets; // cada Signal tiene un único valor: la etiqueta (0.0, 1.0, …)
    private final int k;
    private final Metric metric;
    private final Object[] metricParams; // parámetros extra para métricas (p de Minkowski, invCov de Mahalanobis, etc.)


    /**
     * Constructor principal.
     *
     * @param ds           DataSet completo de entrenamiento (cada target es un Signal de dimensión 1).
     * @param k            número de vecinos (impar y >0).
     * @param metric       qué métrica de distancia usar (por ejemplo, EUCLIDEAN, MANHATTAN, CHEBYSHEV, etc.)
     * @param metricParams parámetros extra según la métrica:
     *                     - Para MINKOWSKI: uno (Double) con p.
     *                     - Para MAHALANOBIS: una (double[][]) con la matriz inversa de covarianza.
     *                     - Para otras métricas no clas. “avanzadas”: no se usan (pueden omitirse).
     */
    public KNearestProcessor2(DataSet ds, int k, Metric metric, Object... metricParams) {
        if (k <= 0 || k % 2 == 0) {
            throw new IllegalArgumentException("K debe ser impar y > 0");
        }
        this.trainInputs  = new ArrayList<>(ds.getInputs());
        this.trainTargets = new ArrayList<>(ds.getTargets());
        this.k            = k;
        this.metric       = metric;
        this.metricParams = metricParams;
    }
        
    /**
     * Dado un Signal “query”, predice la etiqueta (Signal de dimensión 1) usando k vecinos más cercanos
     * y la métrica configurada en este objeto.
     */
    public Signal predict(Signal query) {
        // 1) Convertir query a array primitivo
        double[] qArr = query.getValues().stream().mapToDouble(d -> d).toArray();

        // 2) Construir lista de vecinos con su distancia calculada usando la métrica seleccionada
        List<Neighbor> neighbors = new ArrayList<>(trainInputs.size());
        for (int i = 0; i < trainInputs.size(); i++) {
            Signal sTrain = trainInputs.get(i);
            double[] xi = sTrain.getValues().stream().mapToDouble(d -> d).toArray();

            // invocamos al utilitario para calcular la distancia
            double dist;
            if (metric == Metric.MINKOWSKI) {
                // espera que metricParams[0] sea un Double p
                dist = DistanceMetrics.computeDistance(
                		Metric.MINKOWSKI,
                    xi, qArr,
                    metricParams
                );
            }
            else if (metric == Metric.MAHALANOBIS) {
                // espera que metricParams[0] sea la matriz inversa de covarianza double[][]
                dist = DistanceMetrics.computeDistance(
                		Metric.MAHALANOBIS,
                    xi, qArr,
                    metricParams
                );
            }
            else {
                // para otras métricas no necesitan params adicionales
                dist = DistanceMetrics.computeDistance(metric, xi, qArr);
            }

            neighbors.add(new Neighbor(i, dist));
        }      

        // 2) Ordenar por distancia ascendente
        Collections.sort(neighbors, Comparator.comparingDouble(n -> n.distanceSquared));

        // 3) Tomar los K primeros y hacer “majority vote” sobre trainTargets
        //    Asumimos que cada trainTargets.get(i) es un Signal de longitud 1 con la etiqueta:
        //    ej. 0.0, 1.0, 2.0 para clases 0, 1, 2, etc.
        //    Aquí contamos cuántas veces aparece cada etiqueta.
        //    En un caso binario (0 y 1), basta comparar cuántos vecinos de clase 0 vs clase 1.
        Map<Double, Integer> voteCount = new HashMap<>();
        for (int idx = 0; idx < k; idx++) {
            int trainIndex = neighbors.get(idx).index;
            double label = trainTargets.get(trainIndex).getValues().get(0);
            voteCount.put(label, voteCount.getOrDefault(label, 0) + 1);
        }

        // 4) Encontrar la etiqueta con más votos
        double bestLabel = voteCount.entrySet()
            .stream()
            .max(java.util.Map.Entry.comparingByValue())
            .get()
            .getKey();

        // 5) Devolverla en un Signal de dimensión 1
        Signal out = new Signal();
        out.add(bestLabel);
        return out;
    }   

    // Clase interna para guardar índice y distancia
    private static class Neighbor {
        final int index;
        final double distanceSquared;

        Neighbor(int idx, double dist2) {
            this.index = idx;
            this.distanceSquared = dist2;
        }
    }
}

/*
 * … en el método main o donde instancies …

int k = 5;

// 1) Usar distancia Euclidiana (no requiere params extra):
KNearestProcessor2 knnEuclid = new KNearestProcessor2(ds, k, Metric.EUCLIDEAN);

// 2) Usar distancia Manhattan (tampoco requiere params extra):
KNearestProcessor2 knnManh = new KNearestProcessor2(ds, k, Metric.MANHATTAN);

// 3) Usar distancia Chebyshev:
KNearestProcessor2 knnCheb = new KNearestProcessor2(ds, k, Metric.CHEBYSHEV);

// 4) Usar Minkowski con p = 3:
double p = 3.0;
KNearestProcessor2 knnMink = new KNearestProcessor2(ds, k, Metric.MINKOWSKI, p);

// 5) Usar Mahalanobis (necesitas pasar la matriz inversa de covarianza “invCov”):
double[][] invCov = // tu matriz invertida obtenida de datos de entrenamiento ;
KNearestProcessor2 knnMaha = new KNearestProcessor2(ds, k, Metric.MAHALANOBIS, invCov);

// 6) Usar Coseno:
KNearestProcessor2 knnCos  = new KNearestProcessor2(ds, k, Metric.COSINE);

7) Usar Hamming (básico para vectores binarios / discretos):
KNearestProcessor2 knnHam  = new KNearestProcessor2(ds, k, Metric.HAMMING);

// 8) Usar Canberra:
KNearestProcessor2 knnCanb = new KNearestProcessor2(ds, k, Metric.CANBERRA);

// 9) Usar Bray–Curtis:
KNearestProcessor2 knnBC   = new KNearestProcessor2(ds, k, Metric.BRAY_CURTIS);

// 10) Usar Jaccard:
KNearestProcessor2 knnJac  = new KNearestProcessor2(ds, k, Metric.JACCARD);

// 11) Usar Sørensen–Dice:
KNearestProcessor2 knnSD   = new KNearestProcessor2(ds, k, Metric.SORENSEN_DICE);

// 12) Usar Pearson:
KNearestProcessor2 knnPea  = new KNearestProcessor2(ds, k, Metric.PEARSON);

// … luego llamas a knnXXX.predict(querySignal) para cada nuevo punto … 
 * */
