package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.BackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.LogisticTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Enhanced logistic regression example for concentric circles.
 *
 * The original two-dimensional problem is not linearly separable in x,y.
 * This example adds a radial feature:
 *
 *     r2 = x^2 + y^2
 *
 * The model receives [x, y, r2], which makes the inner and outer rings
 * separable by a linear logistic regression model in the transformed feature
 * space. The chart shows the true classes and points close to p = 0.5.
 */
public class LogisticRegressionEnhancedExample {

    public static void main(String[] args) throws Exception {
        int n = 200;
        double innerRadius = 1.0;
        double outerRadius = 2.0;
        double[][] raw = new double[n][3]; // [x, y, label]
        Random rnd = new Random(42);

        for (int i = 0; i < n; i++) {
            double angle = rnd.nextDouble() * 2 * Math.PI;
            double radius = (i < n / 2)
                    ? innerRadius * Math.sqrt(rnd.nextDouble())
                    : outerRadius + rnd.nextDouble() * 0.5;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            int label = (radius <= innerRadius) ? 0 : 1;
            raw[i] = new double[] { x, y, label };
        }

        DataSet ds2D = DataSetBuilder.fromArray(raw, 2);
        DataSet ds = buildRadialFeatureDataSet(raw, ds2D.getTargets());

        double[] initialWeights = {
                rnd.nextGaussian() * 0.1,
                rnd.nextGaussian() * 0.1,
                rnd.nextGaussian() * 0.1
        };
        LogisticRegressionProcessor logreg =
                new LogisticRegressionProcessor(initialWeights, 0.0);

        LogisticTrainer trainer = new BackpropLogisticTrainer();
        int epochs = 2000;
        double learningRate = 0.5;
        int batchSize = 16;

        List<Integer> indexes = IntStream.range(0, n)
                .boxed()
                .collect(Collectors.toList());

        for (int epoch = 1; epoch <= epochs; epoch++) {
            Collections.shuffle(indexes, rnd);
            for (int i = 0; i < n; i += batchSize) {
                int end = Math.min(n, i + batchSize);
                List<Signal> batchInputs = indexes.subList(i, end).stream()
                        .map(ds.getInputs()::get)
                        .collect(Collectors.toList());
                List<Signal> batchTargets = indexes.subList(i, end).stream()
                        .map(ds.getTargets()::get)
                        .collect(Collectors.toList());
                DataSet batch = new DataSet(batchInputs, batchTargets);
                logreg = (LogisticRegressionProcessor)
                        trainer.train(logreg, batch, 1, learningRate);
            }

            if (epoch % 500 == 0) {
                double acc = computeAccuracy(logreg, ds);
                System.out.printf("Epoch %4d/%d  acc=%.2f%%%n",
                        epoch, epochs, acc * 100);
            }
        }

        double finalAccuracy = computeAccuracy(logreg, ds);
        System.out.printf("Final accuracy: %.2f%%%n", finalAccuracy * 100);

        PlotData plotData = buildPlotData(raw, logreg);
        PlotlyBrowserViewer.showInBrowser(buildPlotHtml(plotData));
    }

    private static DataSet buildRadialFeatureDataSet(
            double[][] raw,
            List<Signal> targets) {

        List<Signal> inputs = new ArrayList<>(raw.length);
        for (double[] row : raw) {
            double x = row[0];
            double y = row[1];
            double r2 = x * x + y * y;
            Signal signal = new Signal();
            signal.add(x);
            signal.add(y);
            signal.add(r2);
            inputs.add(signal);
        }
        return new DataSet(inputs, targets);
    }

    private static PlotData buildPlotData(
            double[][] raw,
            LogisticRegressionProcessor model) {

        PlotData data = new PlotData();
        for (double[] row : raw) {
            double x = row[0];
            double y = row[1];
            int actualLabel = (row[2] == 0) ? 0 : 1;

            if (actualLabel == 0) {
                data.x0.add(x);
                data.y0.add(y);
            } else {
                data.x1.add(x);
                data.y1.add(y);
            }

        }

        double limit = findPlotLimit(raw);
        double step = 0.05;
        for (double x = -limit; x <= limit; x += step) {
            for (double y = -limit; y <= limit; y += step) {
                Signal input = radialSignal(x, y);
                double probability = model.predict(input).getValues().get(0);
                if (Math.abs(probability - 0.5) < 0.02) {
                    data.boundaryX.add(x);
                    data.boundaryY.add(y);
                }
            }
        }
        return data;
    }

    private static double findPlotLimit(double[][] raw) {
        double maxAbs = 0.0;
        for (double[] row : raw) {
            maxAbs = Math.max(maxAbs, Math.abs(row[0]));
            maxAbs = Math.max(maxAbs, Math.abs(row[1]));
        }
        return maxAbs + 0.25;
    }

    private static String buildPlotHtml(PlotData data) {
        return """
            <html>
            <head>
              <script src="plotly.min.js"></script>
            </head>
            <body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                const trace0 = {
                  x: %s,
                  y: %s,
                  mode: 'markers',
                  name: 'Class 0 - inner circle',
                  marker: { color: 'blue', size: 6, opacity: 0.7 }
                };
                const trace1 = {
                  x: %s,
                  y: %s,
                  mode: 'markers',
                  name: 'Class 1 - outer ring',
                  marker: { color: 'orange', size: 6, opacity: 0.7 }
                };
                const boundary = {
                  x: %s,
                  y: %s,
                  mode: 'markers',
                  name: 'Boundary p near 0.5',
                  marker: { color: 'black', size: 8, opacity: 0.8 }
                };

                Plotly.newPlot('chart', [trace0, trace1, boundary], {
                  title: 'Enhanced Logistic Regression - radial feature',
                  xaxis: { title: 'x' },
                  yaxis: { title: 'y' }
                });
              </script>
            </body>
            </html>
            """.formatted(
                data.x0, data.y0,
                data.x1, data.y1,
                data.boundaryX, data.boundaryY
            );
    }

    private static Signal radialSignal(double x, double y) {
        Signal signal = new Signal();
        signal.add(x);
        signal.add(y);
        signal.add(x * x + y * y);
        return signal;
    }

    private static double computeAccuracy(
            LogisticRegressionProcessor model,
            DataSet ds) {

        long correct = 0;
        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal input = ds.getInputs().get(i);
            double probability = model.predict(input).getValues().get(0);
            int predicted = (probability > 0.5) ? 1 : 0;
            int actual = ds.getTargets().get(i).getValues().get(0).intValue();
            if (predicted == actual) {
                correct++;
            }
        }
        return correct / (double) ds.getInputs().size();
    }

    private static class PlotData {
        private final List<Double> x0 = new ArrayList<>();
        private final List<Double> y0 = new ArrayList<>();
        private final List<Double> x1 = new ArrayList<>();
        private final List<Double> y1 = new ArrayList<>();
        private final List<Double> boundaryX = new ArrayList<>();
        private final List<Double> boundaryY = new ArrayList<>();
    }
}
