package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.Trainer;
import com.merlab.signals.nn.trainer.Trainer2;
import com.merlab.signals.nn.trainer.TrainerFactory;
import com.merlab.signals.nn.trainer.TrainerFactory2;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * LogisticRegressionSimpleExample2
 *
 * Misma lógica que LogisticRegressionSimpleExample,
 * pero usando la interfaz genérica Trainer<M> y TrainerFactory.
 * Además incluye un ModelReporter al final.
 */
public class LogisticRegressionSimpleExample2 {

    public static void main(String[] args) throws Exception {
        // 1) Generar datos sintéticos linealmente separables
        int N = 200;
        double[][] raw = new double[N][3]; // [x, y, label]
        Random rnd = new Random(42);

        for (int i = 0; i < N; i++) {
            double cx = (i < N/2) ? -1.5 : 1.5;
            double cy = (i < N/2) ? -1.5 : 1.5;
            int    label = (i < N/2) ? 0 : 1;
            double x = cx + rnd.nextGaussian() * 0.5;
            double y = cy + rnd.nextGaussian() * 0.5;
            raw[i] = new double[]{ x, y, label };
        }

        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        // 2) Crear el procesador de regresión logística: 2 entradas + sigmoide
        double[] w0 = { rnd.nextGaussian() * 0.1, rnd.nextGaussian() * 0.1 };
        LogisticRegressionProcessor logreg = new LogisticRegressionProcessor(w0, 0.0);

        // 3) Obtener Trainer desde Factory
        //@SuppressWarnings("unchecked")
        //Trainer<LogisticRegressionProcessor> trainer = (Trainer<LogisticRegressionProcessor>) TrainerFactory.create(Algorithm.LOGISTIC_REGRESSION);
        
        Trainer2<LogisticRegressionProcessor> trainer = TrainerFactory2.createLogisticTrainer();


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
                logreg = trainer.train(logreg, batch, 1, lr);
            }
            if (e % 500 == 0) {
                double acc = computeAccuracy(logreg, ds);
                System.out.printf("Epoch %4d/%d  acc=%.2f%%%n", e, epochs, acc * 100);
            }
        }

        // 4) Evaluación final y Reporte de Modelo
        double finalAcc = computeAccuracy(logreg, ds);
        ModelInfo info = new ModelInfo.Builder("Logistic Regression Simple")
            .addLayer(2, 1, "Sigmoide")
            .epochs(epochs)
            .learningRate(lr)
            .accuracy(finalAcc * 100)
            .build();
        ModelReporter.report(info);

        // 5) Separar puntos para graficar
        List<Double> xs0 = new ArrayList<>(), ys0 = new ArrayList<>();
        List<Double> xs1 = new ArrayList<>(), ys1 = new ArrayList<>();
        List<Double> fx  = new ArrayList<>(), fy  = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            double x = raw[i][0], y = raw[i][1];
            Signal s = new Signal();
            s.add(x); s.add(y);
            double prob = logreg.predict(s).getValues().get(0);
            int predLabel = (prob > 0.5) ? 1 : 0;
            int trueLabel = (int) raw[i][2];

            if (trueLabel == 0) {
                xs0.add(x);
                ys0.add(y);
            } else {
                xs1.add(x);
                ys1.add(y);
            }
            if (Math.abs(prob - 0.5) < 0.02) {
                fx.add(x);
                fy.add(y);
            }
        }

        // 6) Generar HTML con Plotly inline
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
                    title:'Logistic Regression Simple (Factory + Reporter)',
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

/*
package com.merlab.nn.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.Trainer;
import com.merlab.signals.nn.trainer.TrainerFactory;
import com.merlab.signals.plot.PlotlyBrowserViewer;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;

public class LogisticRegressionSimpleExample2 {

    public static void main(String[] args) throws Exception {
        // 1) Generar datos sintéticos (2D separables linealmente)
        int N = 200;
        double[][] raw = new double[N][3];
        Random rnd = new Random(42);
        for (int i = 0; i < N; i++) {
            double x = rnd.nextDouble() * 4 - 2;   // en [-2,2]
            double y = rnd.nextDouble() * 4 - 2;   // en [-2,2]
            // definimos etiqueta según línea y = 0.5*x - 0.2  (por ejemplo)
            int label = (y > 0.5*x - 0.2) ? 1 : 0;
            raw[i] = new double[]{ x, y, label };
        }
        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        // 2) Crear procesador logístico (dos entradas + sigmoide)
        double[] w0 = { rnd.nextGaussian()*0.1, rnd.nextGaussian()*0.1 };
        double b0 = 0.0;
        LogisticRegressionProcessor model = new LogisticRegressionProcessor(w0, b0);

        // 3) Entrenar con el trainer genérico
        Trainer<LogisticRegressionProcessor> trainer =
            TrainerFactory.createLogisticTrainer();

        int epochs = 2000;
        double lr = 0.1;
        int batchSize = 16;
        List<Integer> idx = IntStream.range(0, N).boxed().collect(Collectors.toList());

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
                model = trainer.train(model, batch, 1, lr);
            }
            if (e % 500 == 0) {
                double acc = computeAccuracy(model, ds);
                System.out.printf("Epoch %4d/%d  acc=%.2f%%%n", e, epochs, acc*100);
            }
        }

        // 4) Evaluación final
        double finalAcc = computeAccuracy(model, ds);
        System.out.printf("Accuracy final: %.2f%%%n", finalAcc*100);

        // 5) Reporte
        ModelInfo info = new ModelInfo.Builder("Logistic Regression Simple")
            .addLayer(2, 2, "Sigmoide")
            .epochs(epochs)
            .learningRate(lr)
            .accuracy(finalAcc * 100)
            .build();
        ModelReporter.report(info);

        // 6) Preparar puntos para graficar
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();
        List<Double> fx = new ArrayList<>(), fy = new ArrayList<>();  // frontera

        for (int i = 0; i < N; i++) {
            double xi = raw[i][0];
            double yi = raw[i][1];
            int trueL = (int) raw[i][2];
            Signal s = new Signal();
            s.add(xi); s.add(yi);
            double p = model.predict(s).getValues().get(0);
            int predL = (p > 0.5) ? 1 : 0;

            if (trueL == 0) {
                x0.add(xi); y0.add(yi);
            } else {
                x1.add(xi); y1.add(yi);
            }
            if (Math.abs(p - 0.5) < 0.02) {
                fx.add(xi); fy.add(yi);
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
                const x0 = %s, y0 = %s;
                const x1 = %s, y1 = %s;
                const fx = %s, fy = %s;

                const trace0 = { x:x0, y:y0, mode:'markers', name:'Clase 0',
                                 marker:{color:'blue', size:6, opacity:0.7} };
                const trace1 = { x:x1, y:y1, mode:'markers', name:'Clase 1',
                                 marker:{color:'orange', size:6, opacity:0.7} };
                const traceF = { x:fx, y:fy, mode:'markers', name:'Frontera p≈0.5',
                                 marker:{color:'black', size:8, opacity:0.8} };

                Plotly.newPlot('chart',[trace0, trace1, traceF], {
                  title:'Logistic Regression Simple (lineal)',
                  xaxis:{ title:'x' },
                  yaxis:{ title:'y' }
                });
              </script>
            </body>
            </html>
            """.formatted(
                x0.toString(), y0.toString(),
                x1.toString(), y1.toString(),
                fx.toString(), fy.toString()
            );

        PlotlyBrowserViewer.showInBrowser(html);
    }

    private static double computeAccuracy(
        LogisticRegressionProcessor model,
        DataSet ds
    ) {
        long correct = 0;
        int M = ds.getInputs().size();
        for (int i = 0; i < M; i++) {
            double p = model.predict(ds.getInputs().get(i))
                            .getValues().get(0);
            int pred = (p > 0.5) ? 1 : 0;
            int actual = ds.getTargets().get(i).getValues().get(0).intValue();
            if (pred == actual) correct++;
        }
        return correct / (double) M;
    }
}
*/