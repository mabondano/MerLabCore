package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.BackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.LogisticTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * LogisticRegressionSimpleExample
 *
 * Genera un conjunto de datos linealmente separables (dos clústeres
 * gaussianos), entrena un regresor logístico con Backprop,
 * y dibuja la frontera de decisión usando Plotly inline.
 *
 * Nota:
 * - Esta versión mantiene los nombres de variables de la clase anterior,
 *   pero reemplaza los "círculos concéntricos" por dos grupos gaussianos
 *   separados linealmente.
 * - Se usa LogisticRegressionProcessor (sin “Simple” en el prefijo).
 */
public class LogisticRegressionSimpleExample {

    public static void main(String[] args) throws Exception {
        // 1) Generar datos sintéticos linealmente separables
        //    Dos clústeres gaussianos en 2D:
        //      Clase 0 centrada en (-1.5, -1.5), Clase 1 centrada en (1.5, 1.5)
        int N = 200;
        double[][] raw = new double[N][3]; // [x, y, label]
        Random rnd = new Random(42);

        // Generamos N/2 puntos para la clase 0 y N/2 para la clase 1
        for (int i = 0; i < N; i++) {
            double cx = (i < N/2) ? -1.5 : 1.5;  // centro x
            double cy = (i < N/2) ? -1.5 : 1.5;  // centro y
            int    label = (i < N/2) ? 0 : 1;
            // Mostramos varianza pequeña para cada clúster
            double x = cx + rnd.nextGaussian() * 0.5;
            double y = cy + rnd.nextGaussian() * 0.5;
            raw[i] = new double[]{ x, y, label };
        }

        // Construimos el DataSet directamente con dos features (x, y)
        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        // 2) Crear el procesador de regresión logística: 2 entradas + sigmoide
        //    Inicializamos pesos pequeños aleatorios y bias = 0
        double[] w0 = { rnd.nextGaussian() * 0.1, rnd.nextGaussian() * 0.1 };
        LogisticRegressionProcessor logreg = new LogisticRegressionProcessor(w0, 0.0);

        // 3) Entrenar con BackpropLogisticTrainer (mini‐batch)
        LogisticTrainer trainer = new BackpropLogisticTrainer();
        int epochs    = 2000;
        double lr     = 0.5;
        int batchSize = 16;

        List<Integer> idx = IntStream.range(0, N)
                                     .boxed()
                                     .collect(Collectors.toList());
        for (int e = 1; e <= epochs; e++) {
            Collections.shuffle(idx, rnd);
            for (int i = 0; i < N; i += batchSize) {
                int end = Math.min(N, i + batchSize);
                List<Signal> xb = idx.subList(i, end).stream()
                                     .map(ds.getInputs()::get)
                                     .collect(Collectors.toList());
                List<Signal> yb = idx.subList(i, end).stream()
                                     .map(ds.getTargets()::get)
                                     .collect(Collectors.toList());
                DataSet batch = new DataSet(xb, yb);
                logreg = (LogisticRegressionProcessor)
                         trainer.train(logreg, batch, 1, lr);
            }
            if (e % 500 == 0) {
                double acc = computeAccuracy(logreg, ds);
                System.out.printf("Epoch %4d/%d  acc=%.2f%%%n", e, epochs, acc * 100);
            }
        }

        // 4) Clasificación final y separación de puntos para graficar
        List<Double> xs0 = new ArrayList<>(), ys0 = new ArrayList<>();
        List<Double> xs1 = new ArrayList<>(), ys1 = new ArrayList<>();
        List<Double> fx  = new ArrayList<>(), fy  = new ArrayList<>();  // puntos frontera

        for (int i = 0; i < N; i++) {
            double x = raw[i][0];
            double y = raw[i][1];
            double prob = logreg.predict(ds.getInputs().get(i))
                                 .getValues().get(0);
            int predLabel = (prob > 0.5) ? 1 : 0;
            int trueLabel = (int) raw[i][2];

            // Separar en dos listas para graficar
            if (trueLabel == 0) {
                xs0.add(x);
                ys0.add(y);
            } else {
                xs1.add(x);
                ys1.add(y);
            }

            // Si la probabilidad está cerca de 0.5, anotamos en “frontera”
            if (Math.abs(prob - 0.5) < 0.02) {
                fx.add(x);
                fy.add(y);
            }
        }

        // 5) Graficar con Plotly inline
        String html = """
            <html>
            <head>
              <script src="plotly.min.js"></script>
            </head>
            <body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                const x0  = %s;
                const y0  = %s;
                const x1  = %s;
                const y1  = %s;
                const fx  = %s;
                const fy  = %s;

                const trace0 = {
                  x: x0, y: y0,
                  mode:'markers',
                  name:'Clase 0',
                  marker:{color:'blue', size:6, opacity:0.7}
                };
                const trace1 = {
                  x: x1, y: y1,
                  mode:'markers',
                  name:'Clase 1',
                  marker:{color:'orange', size:6, opacity:0.7}
                };
                const traceF = {
                  x: fx, y: fy,
                  mode:'markers',
                  name:'Frontera p≈0.5',
                  marker:{color:'black', size:4, opacity:0.6}
                };

                Plotly.newPlot('chart',
                  [trace0, trace1, traceF],
                  {
                    title:'Logistic Regression Simple (linealmente separable)',
                    xaxis:{ title:'x' },
                    yaxis:{ title:'y' }
                  }
                );
              </script>
            </body>
            </html>
            """.formatted(
                xs0.toString(), ys0.toString(),
                xs1.toString(), ys1.toString(),
                fx.toString(), fy.toString()
            );

        PlotlyBrowserViewer.showInBrowser(html);
    }

    private static double computeAccuracy(LogisticRegressionProcessor model, DataSet ds) {
        long correct = 0;
        for (int i = 0; i < ds.getInputs().size(); i++) {
            double prob = model.predict(ds.getInputs().get(i)).getValues().get(0);
            int pred = (prob > 0.5) ? 1 : 0;
            int actual = ds.getTargets().get(i).getValues().get(0).intValue();
            if (pred == actual) correct++;
        }
        return correct / (double) ds.getInputs().size();
    }
}
