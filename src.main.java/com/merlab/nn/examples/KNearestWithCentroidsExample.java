package com.merlab.nn.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.ml.DistanceMetric;
import com.merlab.signals.ml.KMeansProcessor2;
import com.merlab.signals.ml.KNearestProcessor;
import com.merlab.signals.ml.KNearestProcessor2;
import com.merlab.signals.plot.PlotlyBrowserViewer;

/**
 * Ejemplo que combina KMeans (para ver centroides) + KNN para clasificación.
 */
public class KNearestWithCentroidsExample {

    public static void main(String[] args) throws Exception {
        // 1) Crear datos sintéticos (dos clases, linealmente separables)
        int N = 200;
        double[][] raw = new double[N][3];
        Random rnd = new Random(123);

        for (int i = 0; i < N; i++) {
            double x = rnd.nextDouble() * 4 - 2;   // en [-2,2]
            double y = rnd.nextDouble() * 4 - 2;   // en [-2,2]
            // Etiqueta según la recta y = 0.5*x - 0.2
            int label = (y > 0.5 * x - 0.2) ? 1 : 0;
            raw[i] = new double[]{ x, y, label };
        }

        // 2) Construir DataSet a partir de raw
        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        // 3) Aplicar K-means para ver centroides (usamos k=3 y 50 iteraciones máximas)
        int kMeansK = 3;
        KMeansProcessor2 kmeans = new KMeansProcessor2(kMeansK, 50, 42L);
        // Entrenamos solo con las entradas (se ignoran las etiquetas)
        kmeans.fit(ds.getInputs());
        List<double[]> centroides = kmeans.getCentroids();

        // 4) Instanciar KNN con k=5 (usamos el mismo ds para la “fase de entrenamiento”)
        int k = 5;
        KNearestProcessor knn = new KNearestProcessor(ds, k, DistanceMetric.EUCLIDEAN);

        // 5) Calcular accuracy final sobre todo el conjunto:
        int correct = 0;
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();

        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal xi = ds.getInputs().get(i);
            double trueLabel = ds.getTargets().get(i).getValues().get(0);
            double predLabel = knn.predict(xi).getValues().get(0);

            if (trueLabel == 0.0) {
                x0.add(xi.getValues().get(0));
                y0.add(xi.getValues().get(1));
            } else {
                x1.add(xi.getValues().get(0));
                y1.add(xi.getValues().get(1));
            }

            if (predLabel == trueLabel) {
                correct++;
            }
        }

        double accuracy = 100.0 * correct / ds.getInputs().size();
        System.out.printf("Accuracy KNN (k=%d): %.2f%%%n", k, accuracy);

        // 6) Ahora extraer las coordenadas de los centroides
        List<Double> cx = new ArrayList<>(kMeansK);
        List<Double> cy = new ArrayList<>(kMeansK);
        for (double[] centro : centroides) {
            cx.add(centro[0]);
            cy.add(centro[1]);
        }

        // 7) Graficar con Plotly:
        //    - Clase 0 en azul
        //    - Clase 1 en naranja
        //    - Centroides (kMeans) en “triángulos rojos grandes”
        String html = """
            <html>
            <head>
              <script src="plotly.min.js"></script>
            </head>
            <body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                const x0 = %s, y0 = %s;
                const x1 = %s, y1 = %s;
                const cx = %s, cy = %s;

                const trace0 = {
                  x: x0, 
                  y: y0, 
                  mode:'markers',
                  name:'Clase 0', 
                  marker:{ color:'blue', size:6, opacity:0.7 }
                };
                const trace1 = {
                  x: x1, 
                  y: y1, 
                  mode:'markers',
                  name:'Clase 1', 
                  marker:{ color:'orange', size:6, opacity:0.7 }
                };
                const traceCentroides = {
                  x: cx,
                  y: cy,
                  mode:'markers',
                  name:'Centroides k-means',
                  marker:{ color:'red', symbol:'triangle-up', size:12, opacity:0.9 }
                };

                Plotly.newPlot('chart', [trace0, trace1, traceCentroides], {
                  title: 'KNN con centroides (kNN k=%d, K-Means k=%d), Accuracy=%.2f%%',
                  xaxis:{ title:'x' },
                  yaxis:{ title:'y' }
                });
              </script>
            </body>
            </html>
            """.formatted(
                x0.toString(), y0.toString(),
                x1.toString(), y1.toString(),
                cx.toString(),  cy.toString(),
                k,               kMeansK,      accuracy
            );

        PlotlyBrowserViewer.showInBrowser(html);
    }
}
