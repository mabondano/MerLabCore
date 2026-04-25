package com.merlab.signals.core;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

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
        // Simula el plot: simplemente imprime el título y los primeros valores.
        System.out.println("[Plot] " + title + ": " + signal.getValues().subList(0, Math.min(5, signal.getValues().size())) + "...");
    	
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
    
    /**
     * Grafica la señal dada en una ventana con tema y tipo especificado.
     *
     * @param title  título del gráfico
     * @param signal señal a graficar
     * @param theme  tema de XChart (MATLAB, GGPlot2, etc.)
     * @param type   tipo de gráfico (LINE, SCATTER o BAR)
     */
	public static void plotSignal2(
	        String title,
	        Signal signal,
	        ChartTheme theme,
	        ChartType type
	) {
	    // Prepara los datos
	    List<Integer> xData = IntStream.range(0, signal.size())
	            .boxed().collect(Collectors.toList());
	    List<Double> yData = signal.getValues();
	
	    if (type == ChartType.BAR) {
	        // --- Gráfico de barras ---
	        List<String> xs = xData.stream()
	                .map(Object::toString)
	                .collect(Collectors.toList());
	
	        CategoryChart chart = new CategoryChartBuilder()
	                .width(800).height(600)
	                .title(title)
	                .xAxisTitle("Índice").yAxisTitle("Valor")
	                .theme(theme)
	                .build();
	
	        // Añade la serie como barras
	        CategorySeries cs = chart.addSeries("Señal", xs, yData);
	        cs.setChartCategorySeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Bar);
	        cs.setMarker(SeriesMarkers.CIRCLE);
	
	        new SwingWrapper<>(chart).displayChart();
	
	    } else {
	        // --- Gráfico XY (línea o dispersión) ---
	        XYChart chart = new XYChartBuilder()
	                .width(800).height(600)
	                .title(title)
	                .xAxisTitle("Índice").yAxisTitle("Valor")
	                .theme(theme)
	                .build();
	
	        XYSeries series = chart.addSeries("Señal", xData, yData);
	
	        if (type == ChartType.SCATTER) {
	            series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
	        } else {
	            series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
	        }
	
	        // Control de marcadores: ninguno en línea, círculo en scatter
	        series.setMarker(type == ChartType.SCATTER
	                ? SeriesMarkers.CIRCLE
	                : SeriesMarkers.NONE);
	
	        new SwingWrapper<>(chart).displayChart();
	    }
	}
}
    	
