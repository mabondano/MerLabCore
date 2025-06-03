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
import com.merlab.signals.ml.KNearestProcessor;
import com.merlab.signals.nn.trainer.Trainer2;
import com.merlab.signals.nn.trainer.TrainerFactory.Algorithm;
import com.merlab.signals.nn.trainer.TrainerFactory2;
import com.merlab.signals.plot.PlotlyBrowserViewer;

/**
 * Ejemplo de clasificación binaria con KNN (2D sintético linealmente separable).
 */
public class KNearestExample2 {

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
        //    DataSetBuilder.fromArray(raw, 2) asume columnas [x, y, label],
        //    donde “2” indica que las primeras 2 columnas son features y la última es la etiqueta.
        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        // 3) Instanciar el Processor KNN con K = 5 (por ejemplo)
        int k = 5;
        KNearestProcessor knn = new KNearestProcessor(ds, k, DistanceMetric.EUCLIDEAN);

        // 4) Calcular accuracy final sobre todo el conjunto:
        int correct = 0;
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();
        List<Double> fx = new ArrayList<>(), fy = new ArrayList<>(); // frontera
        // (En KNN puro no definimos frontera continua aquí, así que omitimos fx/fy.)


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
            // Si la predicción está “muy cerca” de 0.5, lo marcamos como frontera
            // (en este caso, como KNN devuelve 0 o 1, no usamos probabilidad—pero
            // podemos optar por marcar puntos que cambian de clase al variar K, etc.
            // Para simplicidad, solo marca los que queden a la distancia exacta de voto K/2,
            // pero aquí omitiremos ese detalle y no graficaremos frontera continua).
        }

        double accuracy = 100.0 * correct / ds.getInputs().size();
        System.out.printf("Accuracy KNN (k=%d): %.2f%%%n", k, accuracy);

        // 5) Graficar con Plotly: Clase 0 en azul, Clase 1 en naranja, sin frontera continua
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

                    Plotly.newPlot('chart', [trace0, trace1], {
                      title: 'KNN Clasificación (k=%d), Accuracy=%.2f%%',
                      xaxis:{ title:'x' },
                      yaxis:{ title:'y' }
                    });
                  </script>
                </body>
                </html>
                """.formatted(
                    x0.toString(), y0.toString(),
                    x1.toString(), y1.toString(),
                    k, accuracy
                );

        PlotlyBrowserViewer.showInBrowser(html);
    }
}
