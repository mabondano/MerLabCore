package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
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
 * Logistic regression example with a linearly separable 2D dataset.
 */
public class LogisticRegressionLinearExample {

    public static void main(String[] args) {
        int n = 240;
        double[][] raw = new double[n][3];
        Random rnd = new Random(7);

        for (int i = 0; i < n; i++) {
            double x = -2.0 + 4.0 * rnd.nextDouble();
            double y = -2.0 + 4.0 * rnd.nextDouble();
            int label = x + y > 0.0 ? 1 : 0;
            raw[i] = new double[] { x, y, label };
        }

        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        double[] initialWeights = {
            rnd.nextGaussian() * 0.1,
            rnd.nextGaussian() * 0.1
        };
        LogisticRegressionProcessor model = new LogisticRegressionProcessor(initialWeights, 0.0);

        LogisticTrainer trainer = new BackpropLogisticTrainer();
        int epochs = 1200;
        double learningRate = 0.15;
        LogisticRegressionProcessor trained = trainer.train(model, ds, epochs, learningRate);

        double accuracy = computeAccuracy(trained, ds);
        System.out.printf("Logistic regression linear accuracy: %.2f%%%n", accuracy * 100.0);
        System.out.println("Weights: " + Arrays.toString(trained.getWeights()));
        System.out.println("Bias: " + trained.getBias());

        plotResults(trained, ds);
    }

    private static double computeAccuracy(LogisticRegressionProcessor model, DataSet ds) {
        long correct = 0;
        for (int i = 0; i < ds.getInputs().size(); i++) {
            double p = model.predict(ds.getInputs().get(i)).getValues().get(0);
            int predicted = p > 0.5 ? 1 : 0;
            int actual = (int) ds.getTargets().get(i).getValues().get(0).doubleValue();
            if (predicted == actual) {
                correct++;
            }
        }
        return correct / (double) ds.getInputs().size();
    }

    private static void plotResults(LogisticRegressionProcessor model, DataSet ds) {
        List<Double> actualX0 = new ArrayList<>();
        List<Double> actualY0 = new ArrayList<>();
        List<Double> actualX1 = new ArrayList<>();
        List<Double> actualY1 = new ArrayList<>();
        List<Double> predictedX0 = new ArrayList<>();
        List<Double> predictedY0 = new ArrayList<>();
        List<Double> predictedX1 = new ArrayList<>();
        List<Double> predictedY1 = new ArrayList<>();

        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal input = ds.getInputs().get(i);
            double x = input.getValues().get(0);
            double y = input.getValues().get(1);
            int actual = (int) ds.getTargets().get(i).getValues().get(0).doubleValue();
            double probability = model.predict(input).getValues().get(0);
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
            .title("Logistic Regression Linear Classification")
            .xAxisTitle("x")
            .yAxisTitle("y")
            .theme(ChartTheme.Matlab)
            .build();

        addSeriesIfNotEmpty(chart, "Actual 0", actualX0, actualY0, SeriesMarkers.CIRCLE);
        addSeriesIfNotEmpty(chart, "Actual 1", actualX1, actualY1, SeriesMarkers.CROSS);
        addSeriesIfNotEmpty(chart, "Predicted 0", predictedX0, predictedY0, SeriesMarkers.DIAMOND);
        addSeriesIfNotEmpty(chart, "Predicted 1", predictedX1, predictedY1, SeriesMarkers.SQUARE);

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
