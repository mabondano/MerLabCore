package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetIO;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.plot.ChartType;
import com.merlab.signals.plot.SignalPlotter;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MLPRegressionExample4:
 * - Igual que la versión 3, pero dibuja Real vs Predicción en la misma gráfica.
 */
public class MLPRegressionExample4 {

    public static void main(String[] args) throws Exception {
        // 1) Generar dataset de un solo feature x
        int n = 100;
        List<Signal> inputs  = new ArrayList<>();
        Signal real = new Signal();
        for (int i = 0; i < n; i++) {
            double x = 2 * Math.PI * i / (n - 1);
            Signal in = new Signal();
            in.add(x);
            inputs.add(in);
            real.add(Math.sin(x));
        }

        // 2) Inicializar pesos y biases aleatorios pequeños
        Random rnd = new Random(123);
        double[][] wH = new double[10][1];
        double[]   bH = new double[10];
        for (int i = 0; i < 10; i++) {
            wH[i][0] = (rnd.nextDouble()*2-1)*0.1;
            bH[i]     = (rnd.nextDouble()*2-1)*0.1;
        }
        double[][] wO = new double[1][10];
        double[]   bO = new double[1];
        for (int j = 0; j < 10; j++) {
            wO[0][j] = (rnd.nextDouble()*2-1)*0.1;
        }
        bO[0] = (rnd.nextDouble()*2-1)*0.1;

        Layer hidden = new Layer(wH, bH, ActivationFunctions.RELU);
        Layer output = new Layer(wO, bO, ActivationFunctions.IDENTITY);
        NeuralNetworkProcessor mlp = new MultiLayerPerceptronProcessor(List.of(hidden, output));

        // 3) Calcular predicciones
        Signal pred = new Signal();
        for (Signal in : inputs) {
            pred.add(mlp.predict(in).getValues().get(0));
        }

        // 4) Graficar ambas series en un XYChart
        List<Integer> xData = new ArrayList<>();
        for (int i = 0; i < n; i++) xData.add(i);

        XYChart chart = new XYChartBuilder()
            .width(800).height(600)
            .title("Regresión sin(x): Real vs Predicción")
            .xAxisTitle("Índice")
            .yAxisTitle("Valor")
            .theme(ChartTheme.Matlab)
            .build();

        // Serie real
        var sReal = chart.addSeries("Real sin(x)", xData, real.getValues());
        sReal.setXYSeriesRenderStyle(org.knowm.xchart.XYSeries.XYSeriesRenderStyle.Line);
        sReal.setMarker(SeriesMarkers.NONE);

        // Serie predicción
        var sPred = chart.addSeries("Predicción MLP", xData, pred.getValues());
        sPred.setXYSeriesRenderStyle(org.knowm.xchart.XYSeries.XYSeriesRenderStyle.Line);
        sPred.setMarker(SeriesMarkers.CIRCLE);

        new org.knowm.xchart.SwingWrapper<>(chart).displayChart();
    }
}
