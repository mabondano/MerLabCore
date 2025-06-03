package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.ml.HierarchicalClusteringProcessor;
import com.merlab.signals.ml.HierarchicalClusteringProcessor.Cluster;
import com.merlab.signals.ml.HierarchicalClusteringProcessor.Fusion;
import com.merlab.signals.nn.distance.DistanceMetrics.Metric;

import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Ejemplo de Hierarchical Clustering (single-linkage) sobre datos sintéticos en 2D.
 * - Genera dos clusters sintéticos.
 * - Usa HierarchicalClusteringProcessor para agrupar en 2 clusters finales.
 * - Muestra en consola la asignación final y (opcional) grafica en Plotly.
 */
public class HierarchicalExample2 {

    public static void main(String[] args) throws Exception {
        // 1) Generar datos sintéticos
        int N = 200;
        double[][] raw = new double[N][2];
        Random rnd = new Random(2025);

        for (int i = 0; i < N; i++) {
            if (i < N/2) {
                raw[i][0] = rnd.nextGaussian() * 0.5 + 0.0;
                raw[i][1] = rnd.nextGaussian() * 0.5 + 0.0;
            } else {
                raw[i][0] = rnd.nextGaussian() * 0.5 + 3.0;
                raw[i][1] = rnd.nextGaussian() * 0.5 + 3.0;
            }
        }

        // 2) Convertir a DataSet (con etiqueta dummy, irrelevante aquí)
        double[][] rawWithDummy = new double[N][3];
        for (int i = 0; i < N; i++) {
            rawWithDummy[i][0] = raw[i][0];
            rawWithDummy[i][1] = raw[i][1];
            rawWithDummy[i][2] = 0.0; // dummy
        }
        DataSet ds = DataSetBuilder.fromArray(rawWithDummy, 2);

        // 3) Instanciar HierarchicalClusteringProcessor (euclidiana por defecto)
        HierarchicalClusteringProcessor hcp =
            new HierarchicalClusteringProcessor(ds, Metric.EUCLIDEAN);

        // 4) Hacer fit para terminar en 2 clusters
        int targetClusters = 2;
        List<Cluster> finalClusters = hcp.fit(targetClusters);

        // 5) Mostrar en consola la asignación final (cada cluster → índices de puntos)
        System.out.println("Clusters finales (índices de puntos):");
        for (int c = 0; c < finalClusters.size(); c++) {
            Cluster cluster = finalClusters.get(c);
            System.out.printf("  Cluster %d: %s%n", c, cluster.indices);
        }

        // (Opcional) Mostrar el dendrograma completo de fusiones:
        System.out.println("\nDendrograma (listado de fusiones en cada paso):");
        for (Fusion f : hcp.getDendrogram()) {
            System.out.printf(" Fusion: C%s + C%s → C%s   (dist=%.3f)%n",
                f.c1.indices, f.c2.indices, f.merged.indices, f.distance);
        }

        // 6) Asignar color a cada punto según cluster final para graficar
        //    Primer cluster → color azul, segundo → naranja
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();

        // Recorremos cada cluster y, dentro de él, cada índice de punto:
        for (int c = 0; c < finalClusters.size(); c++) {
            Cluster cluster = finalClusters.get(c);
            for (int idx : cluster.indices) {
                double px = raw[idx][0];
                double py = raw[idx][1];
                if (c == 0) {
                    x0.add(px);
                    y0.add(py);
                } else {
                    x1.add(px);
                    y1.add(py);
                }
            }
        }

        // 7) Graficar con Plotly
        String html = """
            <html>
            <head>
              <script src="plotly.min.js"></script>
            </head>
            <body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                const x0 = %s;
                const y0 = %s;
                const x1 = %s;
                const y1 = %s;

                const trace0 = {
                  x: x0, y: y0,
                  mode:'markers',
                  name:'Cluster A',
                  marker:{color:'blue', size:6, opacity:0.7}
                };
                const trace1 = {
                  x: x1, y: y1,
                  mode:'markers',
                  name:'Cluster B',
                  marker:{color:'orange', size:6, opacity:0.7}
                };

                Plotly.newPlot('chart', [trace0, trace1], {
                  title:'Hierarchical Clustering (single-link, k=2)',
                  xaxis:{title:'Coord X'},
                  yaxis:{title:'Coord Y'}
                });
              </script>
            </body>
            </html>
            """.formatted(
                x0.toString(), y0.toString(),
                x1.toString(), y1.toString()
            );

        PlotlyBrowserViewer.showInBrowser(html);
    }
}
