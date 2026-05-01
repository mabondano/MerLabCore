package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.Trainer2;
import com.merlab.signals.nn.trainer.TrainerFactory2;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * LogisticRegressionSimpleExample2.
 *
 * Same idea as LogisticRegressionSimpleExample, using Trainer2 and TrainerFactory2.
 * Also reports model information at the end.
 */
public class LogisticRegressionSimpleExample2 {

    public static void main(String[] args) throws Exception {
        int n = 200;
        double[][] raw = new double[n][3];
        Random rnd = new Random(42);

        for (int i = 0; i < n; i++) {
            double cx = (i < n / 2) ? -1.5 : 1.5;
            double cy = (i < n / 2) ? -1.5 : 1.5;
            int label = (i < n / 2) ? 0 : 1;
            double x = cx + rnd.nextGaussian() * 0.5;
            double y = cy + rnd.nextGaussian() * 0.5;
            raw[i] = new double[] { x, y, label };
        }

        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        double[] w0 = { rnd.nextGaussian() * 0.1, rnd.nextGaussian() * 0.1 };
        LogisticRegressionProcessor logreg = new LogisticRegressionProcessor(w0, 0.0);

        Trainer2<LogisticRegressionProcessor> trainer = TrainerFactory2.createLogisticTrainer();

        int epochs = 2000;
        double learningRate = 0.5;
        int batchSize = 16;

        List<Integer> idx = IntStream.range(0, n)
            .boxed()
            .collect(Collectors.toList());
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
                logreg = trainer.train(logreg, batch, 1, learningRate);
            }
            if (e % 500 == 0) {
                double acc = computeAccuracy(logreg, ds);
                System.out.printf("Epoch %4d/%d  acc=%.2f%%%n", e, epochs, acc * 100);
            }
        }

        double finalAcc = computeAccuracy(logreg, ds);
        ModelInfo info = new ModelInfo.Builder("Logistic Regression Simple")
            .addLayer(2, 1, "Sigmoid")
            .epochs(epochs)
            .learningRate(learningRate)
            .accuracy(finalAcc * 100)
            .build();
        ModelReporter.report(info);

        List<Double> xs0 = new ArrayList<>();
        List<Double> ys0 = new ArrayList<>();
        List<Double> xs1 = new ArrayList<>();
        List<Double> ys1 = new ArrayList<>();
        List<Double> fx = new ArrayList<>();
        List<Double> fy = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double x = raw[i][0];
            double y = raw[i][1];
            Signal s = new Signal();
            s.add(x);
            s.add(y);
            double prob = logreg.predict(s).getValues().get(0);
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
                  name:'Class 0',
                  marker:{color:'blue', size:6, opacity:0.7}
                };
                const trace1 = {
                  x: x1, y: y1,
                  mode:'markers',
                  name:'Class 1',
                  marker:{color:'orange', size:6, opacity:0.7}
                };
                const traceF = {
                  x: fx, y: fy,
                  mode:'markers',
                  name:'Boundary p ~= 0.5',
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
            if (pred == actual) {
                correct++;
            }
        }
        return correct / (double) ds.getInputs().size();
    }
}
