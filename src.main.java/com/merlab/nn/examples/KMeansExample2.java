package com.merlab.nn.examples;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.ml.KMeansProcessor2;
import com.merlab.signals.plot.PlotlyBrowserViewer;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



/**
 * Ejemplo de K-Means (KMeansProcessor2) sobre datos sintéticos en 2D,
 * adaptado al constructor KMeansProcessor2(int k, int maxIters, long seed).
 */
public class KMeansExample2 {

    public static void main(String[] args) throws Exception {
        // 1) Generar datos sintéticos de 2 clusters “bien separados”
        int N = 200;
        double[][] raw = new double[N][2];
        Random rnd = new Random(123);

        // Primer cluster centrado en (0,0), segundo en (3,3)
        for (int i = 0; i < N; i++) {
            if (i < N/2) {
                raw[i][0] = rnd.nextGaussian() * 0.5 + 0.0;
                raw[i][1] = rnd.nextGaussian() * 0.5 + 0.0;
            } else {
                raw[i][0] = rnd.nextGaussian() * 0.5 + 3.0;
                raw[i][1] = rnd.nextGaussian() * 0.5 + 3.0;
            }
        }

        // 2) Convertir a DataSet (con etiqueta dummy, que no usaremos para K-Means)
        //    rawWithDummy[i][0]=x, [i][1]=y, [i][2]=0.0 (dummy target)
        double[][] rawWithDummy = new double[N][3];
        for (int i = 0; i < N; i++) {
            rawWithDummy[i][0] = raw[i][0];
            rawWithDummy[i][1] = raw[i][1];
            rawWithDummy[i][2] = 0.0;  // etiqueta dummy
        }
        DataSet ds = DataSetBuilder.fromArray(rawWithDummy, 2);

        // 3) Instanciar KMeansProcessor2 con tu constructor: k=2, maxIter=100, seed=12345
        int k = 2;
        int maxIter = 100;
        long seed = 12345L;
        KMeansProcessor2 kmeans = new KMeansProcessor2(k, maxIter, seed);
        
        // 4) Entrenar (fit) sobre la lista de inputs
        kmeans.fit(ds.getInputs());                     // <-- ahora recibe List<Signal>
        int[] labels = kmeans.getLabels();               // <-- obtenemos etiquetas
        List<double[]> centroids = kmeans.getCentroids(); // <-- obtenemos centroides


        // 6) Separar puntos por cluster para graficar
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if (labels[i] == 0) {
                x0.add(raw[i][0]);
                y0.add(raw[i][1]);
            } else {
                x1.add(raw[i][0]);
                y1.add(raw[i][1]);
            }
        }
        // Coordenadas de centroides
        List<Double> cx = new ArrayList<>(k), cy = new ArrayList<>(k);
        for (double[] cen : centroids) {
            cx.add(cen[0]);
            cy.add(cen[1]);
        }

        // 7) Graficar con Plotly (puntos y centroides)
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
                const cx = %s;
                const cy = %s;

                const trace0 = {
                  x: x0, y: y0,
                  mode:'markers',
                  name:'Cluster 0',
                  marker:{color:'blue', size:6, opacity:0.7}
                };
                const trace1 = {
                  x: x1, y: y1,
                  mode:'markers',
                  name:'Cluster 1',
                  marker:{color:'orange', size:6, opacity:0.7}
                };
                const traceC = {
                  x: cx, y: cy,
                  mode:'markers',
                  name:'Centroides',
                  marker:{color:'red', size:12, symbol:'x', line:{width:2}}
                };

                Plotly.newPlot('chart', [trace0, trace1, traceC], {
                  title: 'Ejemplo K-Means (k=2) sobre datos sintéticos',
                  xaxis: { title: 'Coord X' },
                  yaxis: { title: 'Coord Y' }
                });
              </script>
            </body>
            </html>
            """.formatted(
                x0.toString(), y0.toString(),
                x1.toString(), y1.toString(),
                cx.toString(), cy.toString()
            );

        PlotlyBrowserViewer.showInBrowser(html);

        // 8) (Opcional) Reportar centroides finales
        StringBuilder sb = new StringBuilder();
        sb.append("KMeans final (k=").append(k).append("), centroides:\n");
        for (int c = 0; c < centroids.size(); c++) {
            double[] cen = centroids.get(c);
            sb.append("  C").append(c).append(": (")
              .append(String.format("%.3f", cen[0])).append(", ")
              .append(String.format("%.3f", cen[1])).append(")\n");
        }
        System.out.println(sb);
        /*
        ModelInfo info = new ModelInfo.Builder("KMeans k=2")
            .addClusterCentroid(centroids.get(0))
            .addClusterCentroid(centroids.get(1))
            .maxIterations(maxIter)
            .build();
        ModelReporter.report(info);
        */       
        // Construir ModelInfo incluyendo cada centroide:
        ModelInfo info = new ModelInfo.Builder("KMeans k=" + k)
            .epochs(maxIter)
            .learningRate(0.0)    // opcional si no aplica
            .mse(0.0)             // opcional si no aplica
            // Agregar cada centroide
            .addClusterCentroid( centroids.get(0) )
            .addClusterCentroid( centroids.get(1) )
            // … etc. para todos los centroides …
            .build();

        ModelReporter.report(info);
    }
}
