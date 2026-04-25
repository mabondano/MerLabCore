package com.merlab.signals.ml;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Procesador K-Nearest Neighbors que admite tanto distancia Euclidiana
 * como distancia Manhattan, según el valor de DistanceMetric.
 */
public class KNearestProcessor {

    private final List<Signal> trainInputs;
    private final List<Signal> trainTargets; // cada Signal es de dimensión 1 con la etiqueta
    private final int k;
    private final DistanceMetric metric;

    /**
     * Constructor “de entrenamiento”: almacena el DataSet completo, el valor de k
     * y la métrica de distancia deseada.
     *
     * @param ds      DataSet con inputs y targets (cada target es un Signal de dimensión 1)
     * @param k       número de vecinos a considerar (debe ser impar para evitar empates)
     * @param metric  métrica de distancia: EUCLIDEAN o MANHATTAN
     */
    public KNearestProcessor(DataSet ds, int k, DistanceMetric metric) {
        if (k <= 0 || k % 2 == 0) {
            throw new IllegalArgumentException("K debe ser impar y > 0");
        }
        this.trainInputs  = new ArrayList<>(ds.getInputs());
        this.trainTargets = new ArrayList<>(ds.getTargets());
        this.k = k;
        this.metric = metric;
    }

    /**
     * Dado un nuevo Signal “query”, devuelve un Signal de dimensión 1 que contiene
     * la categoría predicha (etiqueta) como un Double (ej. 0.0, 1.0, 2.0, …).
     */
    public Signal predict(Signal query) {
        // 1) Construir lista de (índice, distancia) para cada punto de entrenamiento
        List<Neighbor> neighbors = new ArrayList<>(trainInputs.size());
        double[] qArr = query.getValues().stream().mapToDouble(d -> d).toArray();

        for (int i = 0; i < trainInputs.size(); i++) {
            double[] xi = trainInputs.get(i)
                                     .getValues()
                                     .stream()
                                     .mapToDouble(d -> d)
                                     .toArray();
            double dist = computeDistance(xi, qArr);
            neighbors.add(new Neighbor(i, dist));
        }

        // 2) Ordenar por distancia ascendente
        Collections.sort(neighbors, Comparator.comparingDouble(n -> n.distance));

        // 3) Tomar los K primeros y hacer “majority vote” sobre trainTargets
        Map<Double, Integer> voteCount = new HashMap<>();
        for (int idx = 0; idx < k; idx++) {
            int trainIndex = neighbors.get(idx).index;
            double label = trainTargets.get(trainIndex).getValues().get(0);
            voteCount.put(label, voteCount.getOrDefault(label, 0) + 1);
        }

        // 4) Etiqueta con más votos
        double bestLabel = voteCount.entrySet()
                                    .stream()
                                    .max(Map.Entry.comparingByValue())
                                    .get()
                                    .getKey();

        // 5) Devolverla en un Signal de dimensión 1
        Signal out = new Signal();
        out.add(bestLabel);
        return out;
    }

    /**
     * Calcula la distancia entre dos vectores según la métrica seleccionada.
     * - EUCLIDEAN: raíz de la suma de cuadrados
     * - MANHATTAN: suma de valores absolutos
     */
    private double computeDistance(double[] a, double[] b) {
        if (metric == DistanceMetric.EUCLIDEAN) {
            double sumSq = 0.0;
            for (int i = 0; i < a.length; i++) {
                double diff = a[i] - b[i];
                sumSq += diff * diff;
            }
            return Math.sqrt(sumSq);
        } else { // MANHATTAN
            double sumAbs = 0.0;
            for (int i = 0; i < a.length; i++) {
                sumAbs += Math.abs(a[i] - b[i]);
            }
            return sumAbs;
        }
    }

    // Clase interna para guardar índice y distancia calculada
    private static class Neighbor {
        final int index;
        final double distance;

        Neighbor(int idx, double dist) {
            this.index = idx;
            this.distance = dist;
        }
    }
}
