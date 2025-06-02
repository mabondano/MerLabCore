package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.ConfigPerceptronProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import org.knowm.xchart.*;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Grafica con XChart las salidas de AND, OR y XOR
 */
public class LogicGateChartExample {

    public static void main(String[] args) {
        // 1) Define tu perceptrón AND, OR, XOR (XOR fallará)
        NeuralNetworkProcessor andProc = new ConfigPerceptronProcessor(
            new double[]{1.0, 1.0}, -1.5, ActivationFunctions.BINARY_STEP);
        NeuralNetworkProcessor orProc  = new ConfigPerceptronProcessor(
            new double[]{1.0, 1.0}, -0.5, ActivationFunctions.BINARY_STEP);
        NeuralNetworkProcessor xorProc = new ConfigPerceptronProcessor(
            new double[]{1.0, 1.0}, -0.5, ActivationFunctions.BINARY_STEP);

        // 2) Datos de entrada
        double[][] inputs = {{0,0},{0,1},{1,0},{1,1}};

        // 3) Para cada función, computa listas X, Y y C (color/output)
        plotGate("Perceptrón AND", inputs, andProc);
        plotGate("Perceptrón OR",  inputs, orProc);
        plotGate("Perceptrón XOR", inputs, xorProc);
    }

    private static void plotGate(String title, double[][] inputs, NeuralNetworkProcessor proc) {
        // Obtén las coordenadas y resultados
        List<Double> xData = IntStream.range(0, inputs.length)
            .mapToObj(i -> inputs[i][0]).collect(Collectors.toList());
        List<Double> yData = IntStream.range(0, inputs.length)
            .mapToObj(i -> inputs[i][1]).collect(Collectors.toList());
        List<Double> zData = IntStream.range(0, inputs.length)
            .mapToObj(i -> {
                Signal s = new Signal();
                s.add(inputs[i][0]);
                s.add(inputs[i][1]);
                return proc.predict(s).getValues().get(0);
            }).collect(Collectors.toList());

        // Crea el gráfico
        XYChart chart = new XYChartBuilder()
            .width(400).height(300)
            .title(title)
            .xAxisTitle("Input 1")
            .yAxisTitle("Input 2")
            .build();

        // Usa markers diferentes según la salida (0 o 1)
        List<Integer> idx0 = IntStream.range(0, zData.size())
            .filter(i -> zData.get(i) < 0.5).boxed().collect(Collectors.toList());
        List<Integer> idx1 = IntStream.range(0, zData.size())
            .filter(i -> zData.get(i) >= 0.5).boxed().collect(Collectors.toList());

        // Serie para salida 0
        chart.addSeries("0", 
            idx0.stream().map(inputsIdx -> inputs[inputsIdx][0]).collect(Collectors.toList()),
            idx0.stream().map(inputsIdx -> inputs[inputsIdx][1]).collect(Collectors.toList()))
          .setMarker(SeriesMarkers.CIRCLE)
          .setMarkerColor(java.awt.Color.BLUE)
          .setLineStyle(SeriesLines.NONE);

        // Serie para salida 1
        chart.addSeries("1",
            idx1.stream().map(inputsIdx -> inputs[inputsIdx][0]).collect(Collectors.toList()),
            idx1.stream().map(inputsIdx -> inputs[inputsIdx][1]).collect(Collectors.toList()))
          .setMarker(SeriesMarkers.CROSS)
          .setMarkerColor(java.awt.Color.RED)
          .setLineStyle(SeriesLines.NONE);

        // Muestra el gráfico en una ventana
        new SwingWrapper<>(chart).displayChart();
    }
}
