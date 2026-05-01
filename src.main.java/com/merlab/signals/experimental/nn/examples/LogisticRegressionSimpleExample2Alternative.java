package com.merlab.signals.experimental.nn.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.Trainer;
import com.merlab.signals.nn.trainer.TrainerFactory;
import com.merlab.signals.plot.PlotlyBrowserViewer;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;

/**
 * Alternative LogisticRegressionSimpleExample2 design kept for comparison.
 */
public class LogisticRegressionSimpleExample2Alternative {

    public static void main(String[] args) throws Exception {
        int n = 200;
        double[][] raw = new double[n][3];
        Random rnd = new Random(42);
        for (int i = 0; i < n; i++) {
            double x = rnd.nextDouble() * 4 - 2;
            double y = rnd.nextDouble() * 4 - 2;
            int label = (y > 0.5 * x - 0.2) ? 1 : 0;
            raw[i] = new double[] { x, y, label };
        }
        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        double[] w0 = { rnd.nextGaussian() * 0.1, rnd.nextGaussian() * 0.1 };
        LogisticRegressionProcessor model = new LogisticRegressionProcessor(w0, 0.0);

        Trainer<LogisticRegressionProcessor> trainer = TrainerFactory.createLogisticTrainer();

        int epochs = 2000;
        double learningRate = 0.1;
        int batchSize = 16;
        List<Integer> idx = IntStream.range(0, n).boxed().collect(Collectors.toList());

        for (int e = 1; e <= epochs; e++) {
            Collections.shuffle(idx, rnd);
            for (int i = 0; i < n; i += batchSize) {
                int end = Math.min(n, i + batchSize);
                List<Signal> xb = idx.subList(i, end).stream()
                    .map(ds.getInputs()::get)
                    .collect(Collectors.toList());
                List<Signal> yb = idx.subList(i, end).stream()
                    .map(ds.getTargets()::get)
                    .collect(Collectors.toList());
                DataSet batch = new DataSet(xb, yb);
                model = trainer.train(model, batch, 1, learningRate);
            }
            if (e % 500 == 0) {
                double acc = computeAccuracy(model, ds);
                System.out.printf("Epoch %4d/%d  acc=%.2f%%%n", e, epochs, acc * 100);
            }
        }

        double finalAcc = computeAccuracy(model, ds);
        System.out.printf("Accuracy final: %.2f%%%n", finalAcc * 100);

        ModelInfo info = new ModelInfo.Builder("Logistic Regression Simple Alternative")
            .addLayer(2, 2, "Sigmoid")
            .epochs(epochs)
            .learningRate(learningRate)
            .accuracy(finalAcc * 100)
            .build();
        ModelReporter.report(info);

        List<Double> x0 = new ArrayList<>();
        List<Double> y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>();
        List<Double> y1 = new ArrayList<>();
        List<Double> fx = new ArrayList<>();
        List<Double> fy = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double xi = raw[i][0];
            double yi = raw[i][1];
            int trueLabel = (int) raw[i][2];
            Signal s = new Signal();
            s.add(xi);
            s.add(yi);
            double p = model.predict(s).getValues().get(0);

            if (trueLabel == 0) {
                x0.add(xi);
                y0.add(yi);
            } else {
                x1.add(xi);
                y1.add(yi);
            }
            if (Math.abs(p - 0.5) < 0.02) {
                fx.add(xi);
                fy.add(yi);
            }
        }

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

                const trace0 = { x:x0, y:y0, mode:'markers', name:'Class 0',
                                 marker:{color:'blue', size:6, opacity:0.7} };
                const trace1 = { x:x1, y:y1, mode:'markers', name:'Class 1',
                                 marker:{color:'orange', size:6, opacity:0.7} };
                const traceF = { x:fx, y:fy, mode:'markers', name:'Boundary p ~= 0.5',
                                 marker:{color:'black', size:8, opacity:0.8} };

                Plotly.newPlot('chart',[trace0, trace1, traceF], {
                  title:'Logistic Regression Simple Alternative',
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

    private static double computeAccuracy(LogisticRegressionProcessor model, DataSet ds) {
        long correct = 0;
        int m = ds.getInputs().size();
        for (int i = 0; i < m; i++) {
            double p = model.predict(ds.getInputs().get(i)).getValues().get(0);
            int pred = (p > 0.5) ? 1 : 0;
            int actual = ds.getTargets().get(i).getValues().get(0).intValue();
            if (pred == actual) {
                correct++;
            }
        }
        return correct / (double) m;
    }
}
