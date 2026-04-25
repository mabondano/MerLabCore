package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * MLPRegressionExample5b:
 * - Igual que Example5, pero grafica Real vs Predicción sin modificar listas inmodificables.
 */
public class MLPRegressionExample5b {

    public static void main(String[] args) {
        // 1) Generar raw array [x, sin(x)]
        int n = 100;
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x = 2 * Math.PI * i / (n - 1);
            raw[i][0] = x;
            raw[i][1] = Math.sin(x);
        }

        // 2) Construir DataSet
        DataSet ds = DataSetBuilder.fromArray(raw, 1);

        // 3) Definir MLP con pesos de ejemplo
        double[][] wH = new double[10][1];
        double[]   bH = new double[10];
        double[][] wO = new double[1][10];
        double[]   bO = new double[1];
        for (int i = 0; i < 10; i++) {
            wH[i][0] = 0.1 * (i + 1);
            bH[i]     = 0.05;
            wO[0][i]  = 0.1 * (10 - i);
        }
        bO[0] = 0.0;
        Layer hidden = new Layer(wH, bH, ActivationFunctions.RELU);
        Layer output = new Layer(wO, bO, ActivationFunctions.IDENTITY);
        NeuralNetworkProcessor mlp =
            new MultiLayerPerceptronProcessor(List.of(hidden, output));

        // 4) Preparar listas para graficar
        List<Integer> xData = IntStream.range(0, n).boxed().toList();
        List<Double>  yReal = new ArrayList<>(n);
        List<Double>  yPred = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            double real = ds.getTargets().get(i).getValues().get(0);
            double pred = mlp.predict(ds.getInputs().get(i)).getValues().get(0);
            yReal.add(real);
            yPred.add(pred);
        }

        // 5) Crear chart con dos series
        XYChart chart = new XYChartBuilder()
            .width(800).height(600)
            .title("sin(x) Real vs Predicción")
            .xAxisTitle("Índice").yAxisTitle("Valor")
            .theme(ChartTheme.XChart)
            .build();

        // serie real
        var sR = chart.addSeries("Real", xData, yReal);
        sR.setXYSeriesRenderStyle(
            org.knowm.xchart.XYSeries.XYSeriesRenderStyle.Line);
        sR.setMarker(SeriesMarkers.NONE);

        // serie predicción
        var sP = chart.addSeries("Predicción", xData, yPred);
        sP.setXYSeriesRenderStyle(
            org.knowm.xchart.XYSeries.XYSeriesRenderStyle.Line);
        sP.setMarker(SeriesMarkers.CIRCLE);

        new org.knowm.xchart.SwingWrapper<>(chart).displayChart();
    }
}

