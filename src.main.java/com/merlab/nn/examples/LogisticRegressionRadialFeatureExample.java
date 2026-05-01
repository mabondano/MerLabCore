package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.BackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.LogisticTrainer;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Logistic regression example for concentric classes using a radial feature.
 *
 * The original 2D problem is not linearly separable in (x, y):
 * one class is an inner circle and the other class is an outer ring.
 *
 * To make the problem suitable for a simple LogisticRegressionProcessor,
 * this example transforms each point into one engineered feature:
 *
 *     r^2 = x^2 + y^2
 *
 * The model receives:
 *
 *     input = [r^2]
 *
 * and learns:
 *
 *     label = 1 when r^2 is large
 *     label = 0 when r^2 is small
 *
 * This demonstrates an important machine-learning idea:
 * when a problem is not linear in the original input space, a feature
 * transformation can make it separable in a different space.
 */
public class LogisticRegressionRadialFeatureExample {

    public static void main(String[] args) {
        int n = 240;
        double innerRadius = 1.0;
        double outerRadius = 2.0;
        Random rnd = new Random(42);

        List<Signal> inputs = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        List<Double> plotX = new ArrayList<>();
        List<Double> plotY = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double angle = rnd.nextDouble() * 2.0 * Math.PI;
            boolean outerClass = i >= n / 2;
            double radius = outerClass
                ? outerRadius + rnd.nextDouble() * 0.5
                : innerRadius * Math.sqrt(rnd.nextDouble());

            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            double radiusSquared = x * x + y * y;

            Signal input = new Signal();
            input.add(radiusSquared);
            inputs.add(input);

            Signal target = new Signal();
            target.add(outerClass ? 1.0 : 0.0);
            targets.add(target);

            plotX.add(x);
            plotY.add(y);
        }

        DataSet ds = new DataSet(inputs, targets);

        double[] initialWeights = { rnd.nextGaussian() * 0.1 };
        LogisticRegressionProcessor model = new LogisticRegressionProcessor(initialWeights, 0.0);

        LogisticTrainer trainer = new BackpropLogisticTrainer();
        int epochs = 1000;
        double learningRate = 0.2;
        LogisticRegressionProcessor trained = trainer.train(model, ds, epochs, learningRate);

        double accuracy = computeAccuracy(trained, ds);
        System.out.printf("Logistic regression radial accuracy: %.2f%%%n", accuracy * 100.0);
        System.out.println("Weights: " + Arrays.toString(trained.getWeights()));
        System.out.println("Bias: " + trained.getBias());

        plotResults(trained, ds, plotX, plotY);
    }

    private static double computeAccuracy(LogisticRegressionProcessor model, DataSet ds) {
        long correct = 0;
        for (int i = 0; i < ds.getInputs().size(); i++) {
            double probability = model.predict(ds.getInputs().get(i)).getValues().get(0);
            int predicted = probability > 0.5 ? 1 : 0;
            int actual = (int) ds.getTargets().get(i).getValues().get(0).doubleValue();
            if (predicted == actual) {
                correct++;
            }
        }
        return correct / (double) ds.getInputs().size();
    }

    private static void plotResults(
        LogisticRegressionProcessor model,
        DataSet ds,
        List<Double> plotX,
        List<Double> plotY
    ) {
        List<Double> actualX0 = new ArrayList<>();
        List<Double> actualY0 = new ArrayList<>();
        List<Double> actualX1 = new ArrayList<>();
        List<Double> actualY1 = new ArrayList<>();
        List<Double> predictedX0 = new ArrayList<>();
        List<Double> predictedY0 = new ArrayList<>();
        List<Double> predictedX1 = new ArrayList<>();
        List<Double> predictedY1 = new ArrayList<>();

        for (int i = 0; i < ds.getInputs().size(); i++) {
            double x = plotX.get(i);
            double y = plotY.get(i);
            int actual = (int) ds.getTargets().get(i).getValues().get(0).doubleValue();
            double probability = model.predict(ds.getInputs().get(i)).getValues().get(0);
            int predicted = probability > 0.5 ? 1 : 0;

            if (actual == 0) {
                actualX0.add(x);
                actualY0.add(y);
            } else {
                actualX1.add(x);
                actualY1.add(y);
            }

            if (predicted == 0) {
                predictedX0.add(x);
                predictedY0.add(y);
            } else {
                predictedX1.add(x);
                predictedY1.add(y);
            }
        }

        XYChart chart = new XYChartBuilder()
            .width(800)
            .height(600)
            .title("Logistic Regression with Radial Feature")
            .xAxisTitle("x")
            .yAxisTitle("y")
            .theme(ChartTheme.Matlab)
            .build();

        addSeriesIfNotEmpty(chart, "Actual inner circle", actualX0, actualY0, SeriesMarkers.CIRCLE);
        addSeriesIfNotEmpty(chart, "Actual outer ring", actualX1, actualY1, SeriesMarkers.CROSS);
        addSeriesIfNotEmpty(chart, "Predicted inner", predictedX0, predictedY0, SeriesMarkers.DIAMOND);
        addSeriesIfNotEmpty(chart, "Predicted outer", predictedX1, predictedY1, SeriesMarkers.SQUARE);

        new SwingWrapper<>(chart).displayChart();
    }

    private static void addSeriesIfNotEmpty(
        XYChart chart,
        String name,
        List<Double> x,
        List<Double> y,
        Marker marker
    ) {
        if (!x.isEmpty() && !y.isEmpty()) {
            XYSeries series = chart.addSeries(name, x, y);
            series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            series.setLineStyle(SeriesLines.NONE);
            series.setMarker(marker);
        }
    }
}
