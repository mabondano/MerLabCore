package com.merlab.signals;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.ChartTheme;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utilidad para graficar señales usando XChart.
 */
public class SignalPlotter {

    /**
     * Grafica la señal dada en una ventana con estilo MATLAB.
     *
     * @param title  título del gráfico
     * @param signal señal a graficar
     */
    public static void plotSignal(String title, Signal signal) {
        // Convertir índices a lista de Integer
        List<Integer> xData = IntStream.range(0, signal.size())
                                       .boxed()
                                       .collect(Collectors.toList());
        // Obtener los valores de la señal
        List<Double> yData = signal.getValues();

        // Crear chart con tema MATLAB
        XYChart chart = new XYChartBuilder()
            .width(800)
            .height(600)
            .title(title)
            .xAxisTitle("Índice")
            .yAxisTitle("Valor")
            .theme(ChartTheme.Matlab)
            .build();

        // Añadir la serie de datos
        chart.addSeries("Señal", xData, yData);

        // Mostrar en ventana Swing
        new SwingWrapper<>(chart).displayChart();
    }
}
