package com.merlab.signals.ml;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.distance.DistanceMetrics;
import com.merlab.signals.nn.distance.DistanceMetrics.Metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Agglomerative Hierarchical Clustering (Single-Link).  
 * - Cada punto inicia como su propio cluster.  
 * - En cada paso, se fusionan los dos clusters más cercanos (por distancia mínima de pares).  
 * - Se repite hasta que el número de clusters = targetClusters.  
 *
 * El usuario puede también pedir la dendrograma completa (lista de fusiones).
 */
public class HierarchicalClusteringProcessor {

    /** Representa un cluster: lista de índices de puntos en el DataSet. */
    public static class Cluster {
        public final List<Integer> indices = new ArrayList<>();

        public Cluster(int singleIndex) {
            indices.add(singleIndex);
        }
        public Cluster(List<Integer> combined) {
            indices.addAll(combined);
        }
    }

    /** Estructura para registrar cada fusión en la dendrograma. */
    public static class Fusion {
        public final Cluster c1, c2, merged;
        public final double distance;  // distancia a la que se fusionaron

        public Fusion(Cluster c1, Cluster c2, Cluster merged, double distance) {
            this.c1 = c1;
            this.c2 = c2;
            this.merged = merged;
            this.distance = distance;
        }
    }

    private final List<Signal> inputs;
    private final Metric metric;
    private final Object[] metricParams;

    private final List<Cluster> clusters;
    private final List<Fusion> dendrogram; // registra cada fusión

    /**
     * Constructor.
     *
     * @param ds            DataSet de entrenamiento (inputs, ignoramos targets si existen).
     * @param metric        métrica para calcular distancias.
     * @param metricParams  parámetros opcionales para la métrica (p de Minkowski, invCov de Mahalanobis).
     */
    public HierarchicalClusteringProcessor(DataSet ds, Metric metric, Object... metricParams) {
        this.inputs = ds.getInputs();
        this.metric = metric;
        this.metricParams = metricParams;

        // 1) Inicializar clusters: cada punto → su propio cluster
        this.clusters = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            Cluster c = new Cluster(i);
            clusters.add(c);
        }
        this.dendrogram = new ArrayList<>();
    }

    /**
     * Realiza el clustering hasta obtener targetClusters clusters finales.
     *
     * @param targetClusters  número de clusters en que se desea detener (>=1 y <=nPoints).
     * @return lista de clusters finales.
     */
    public List<Cluster> fit(int targetClusters) {
        int nPoints = inputs.size();
        if (targetClusters < 1 || targetClusters > nPoints) {
            throw new IllegalArgumentException("targetClusters debe estar en [1.." + nPoints + "]");
        }

        // Mientras tengamos más clusters que targetClusters, fusionar
        while (clusters.size() > targetClusters) {
            // 1) Encontrar par de clusters más cercanos (distancia mínima single-link)
            double bestDist = Double.POSITIVE_INFINITY;
            int bestI = -1, bestJ = -1;

            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double dist = distanceBetweenClustersSingleLink(clusters.get(i), clusters.get(j));
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            // 2) Fusionar clusters bestI y bestJ
            Cluster C1 = clusters.get(bestI);
            Cluster C2 = clusters.get(bestJ);

            // crear un nuevo cluster con la unión de índices
            List<Integer> unionIdx = new ArrayList<>(C1.indices);
            unionIdx.addAll(C2.indices);
            Cluster merged = new Cluster(unionIdx);

            // remover primero los dos (atención a índices: remover mayor primero)
            if (bestI > bestJ) {
                clusters.remove(bestI);
                clusters.remove(bestJ);
            } else {
                clusters.remove(bestJ);
                clusters.remove(bestI);
            }
            // agregar el cluster fusionado
            clusters.add(merged);

            // 3) Registrar la fusión en la dendrograma
            dendrogram.add(new Fusion(C1, C2, merged, bestDist));
        }

        return clusters;
    }

    /**
     * Distancia single-link entre dos clusters: 
     * la mínima distancia euclidiana (u otra métrica) entre cualquier par de puntos (i∈C1, j∈C2).
     */
    private double distanceBetweenClustersSingleLink(Cluster c1, Cluster c2) {
        double best = Double.POSITIVE_INFINITY;
        for (int i : c1.indices) {
            double[] xi = inputs.get(i).getValues().stream().mapToDouble(d -> d).toArray();
            for (int j : c2.indices) {
                double[] xj = inputs.get(j).getValues().stream().mapToDouble(d -> d).toArray();
                double dist;
                // si la métrica es Minkowski o Mahalanobis, se deben pasar params
                if (metric == Metric.MINKOWSKI) {
                    dist = DistanceMetrics.computeDistance(
                        Metric.MINKOWSKI, xi, xj, metricParams
                    );
                } else if (metric == Metric.MAHALANOBIS) {
                    dist = DistanceMetrics.computeDistance(
                        Metric.MAHALANOBIS, xi, xj, metricParams
                    );
                } else {
                    dist = DistanceMetrics.computeDistance(metric, xi, xj);
                }
                if (dist < best) {
                    best = dist;
                }
            }
        }
        return best;
    }

    /** Devuelve la lista de fusiones (dendrograma completo). */
    public List<Fusion> getDendrogram() {
        return Collections.unmodifiableList(dendrogram);
    }
}
