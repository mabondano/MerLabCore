package com.merlab.nn.examples;

import com.merlab.signals.nn.processor.ConfigPerceptronProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;

/**
 * Grafica en el plano (x1,x2) la salida de los perceptrones AND, OR y XOR.
 */
public class LogicGateScatterExample {

    public static void main(String[] args) {
        // 1) Define los perceptrones
        NeuralNetworkProcessor andProc = new ConfigPerceptronProcessor(
            new double[]{1.0,1.0}, -1.5, ActivationFunctions.BINARY_STEP);
        NeuralNetworkProcessor orProc = new ConfigPerceptronProcessor(
            new double[]{1.0,1.0}, -0.5, ActivationFunctions.BINARY_STEP);
        NeuralNetworkProcessor xorProc = new ConfigPerceptronProcessor(
            new double[]{1.0,1.0}, -0.5, ActivationFunctions.BINARY_STEP);

        // 2) Datos de entrada
        double[][] inputs = {
            {0,0}, {0,1}, {1,0}, {1,1}
        };

        // 3) Llamamos a la función genérica para cada puerta
        plotClassification("AND Gate", inputs, andProc);
        plotClassification("OR Gate",  inputs, orProc);
        plotClassification("XOR Gate", inputs, xorProc);
    }

    private static void plotClassification(
        String title,
        double[][] inputs,
        NeuralNetworkProcessor proc
    ) {
        // Listas para x,y según salida
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();

        for (double[] in : inputs) {
            double x = in[0], y = in[1];
            double out = proc.predict(
                new com.merlab.signals.core.Signal(List.of(x,y))
            ).getValues().get(0);

            if (out < 0.5) {
                x0.add(x);  y0.add(y);
            } else {
                x1.add(x);  y1.add(y);
            }
        }

        // Crea un scatter chart
        XYChart chart = new XYChartBuilder()
            .width(400).height(400)
            .title(title)
            .xAxisTitle("x1")
            .yAxisTitle("x2")
            .build();

        // Serie para salida = 0 (círculos azules)
        XYSeries zero = chart.addSeries("0", x0, y0);
        zero.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        zero.setMarker(SeriesMarkers.CIRCLE);

        // Serie para salida = 1 (cruces rojas)
        XYSeries one  = chart.addSeries("1", x1, y1);
        one.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        one.setMarker(SeriesMarkers.CROSS);

        // Muestra la ventana
        new SwingWrapper<>(chart).displayChart();
    }
}
