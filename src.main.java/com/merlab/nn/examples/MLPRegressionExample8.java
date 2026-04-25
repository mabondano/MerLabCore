package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * MLPRegressionExample8:
 * - Normaliza x a [-1,1].
 * - Entrena con BackpropMLPTrainer.
 * - Grafica Real vs Predicción usando x normalizado como eje X.
 */
public class MLPRegressionExample8 {

    public static void main(String[] args) throws Exception {
        // 1) Generar raw array [xNorm, sin(x)] donde xNorm ∈ [-1,1]
        int n = 100;
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x     = 2 * Math.PI * i / (n - 1);
            double xNorm = (x - Math.PI) / Math.PI;  // [0,2π] → [-1,1]
            raw[i][0] = xNorm;
            raw[i][1] = Math.sin(x);
        }
        DataSet ds = DataSetBuilder.fromArray(raw, 1);

        // 2) Definir MLP inicial con pesos aleatorios en [-0.1,0.1]
        Random rnd = new Random(123);
        int hiddenSize = 20;
        double[][] wH = new double[hiddenSize][1];
        double[]   bH = new double[hiddenSize];
        double[][] wO = new double[1][hiddenSize];
        double[]   bO = new double[1];
        for (int i = 0; i < hiddenSize; i++) {
            wH[i][0] = (rnd.nextDouble() * 2 - 1) * 0.1;
            bH[i]     = (rnd.nextDouble() * 2 - 1) * 0.1;
            wO[0][i]  = (rnd.nextDouble() * 2 - 1) * 0.1;
        }
        bO[0] = (rnd.nextDouble() * 2 - 1) * 0.1;

        Layer hidden = new Layer(wH, bH, ActivationFunctions.RELU);
        Layer output = new Layer(wO, bO, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor initial =
            new MultiLayerPerceptronProcessor(List.of(hidden, output));

        // 3) Entrenar con backpropagation
        int    epochs       = 5000;
        double learningRate = 0.005;
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        MultiLayerPerceptronProcessor trained =
            trainer.train(initial, ds, epochs, learningRate);

        // 4) Preparar listas xData, yReal, yPred
        List<Double> xData = new ArrayList<>(n);
        List<Double> yReal = new ArrayList<>(n);
        List<Double> yPred = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            double xNorm = raw[i][0];
            double yTrue = raw[i][1];
            double yHat  = trained.predict(ds.getInputs().get(i))
                                    .getValues().get(0);
            xData.add(xNorm);
            yReal.add(yTrue);
            yPred.add(yHat);
        }

        // 5) Ordenar por xData para un trazo suave
        List<Integer> idx = IntStream.range(0, n)
            .boxed()
            .sorted((i, j) -> Double.compare(xData.get(i), xData.get(j)))
            .collect(Collectors.toList());
        List<Double> xs = idx.stream().map(xData::get).collect(Collectors.toList());
        List<Double> rs = idx.stream().map(yReal::get).collect(Collectors.toList());
        List<Double> ps = idx.stream().map(yPred::get).collect(Collectors.toList());

        // 6) Construir y desplegar el gráfico
        XYChart chart = new XYChartBuilder()
            .width(800).height(600)
            .title("sin(x) Real vs Predicción")
            .xAxisTitle("x normalizado")
            .yAxisTitle("Valor")
            .theme(ChartTheme.XChart)
            .build();

        // Serie real
        XYSeries realSeries = chart.addSeries("Real sin(x)", xs, rs);
        realSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        realSeries.setMarker(SeriesMarkers.NONE);

        // Serie predicción
        XYSeries predSeries = chart.addSeries("Predicción MLP", xs, ps);
        predSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        predSeries.setMarker(SeriesMarkers.CIRCLE);

        new SwingWrapper<>(chart).displayChart();
    }
}
