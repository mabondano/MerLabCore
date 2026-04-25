package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;

/**
 * XORMLPExamplePlot:
 * - MLP manual para XOR (igual que XORMLPExample)
 * - Además grafica en modo scatter el output crudo (valor entre 0 y 1)
 */
public class XORMLPExamplePlot {

    public static void main(String[] args) {
        // 1) Definimos las capas como antes
        double[][] w1 = {
            { 10.0,  10.0},
            {-10.0, -10.0}
        };
        double[] b1 = { -5.0, 15.0 };
        Layer hidden = new Layer(w1, b1, ActivationFunctions.SIGMOID);

        double[][] w2 = {{ 10.0, 10.0 }};
        double[] b2 = { -15.0 };
        Layer output = new Layer(w2, b2, ActivationFunctions.SIGMOID);

        NeuralNetworkProcessor mlp = new MultiLayerPerceptronProcessor(
            List.of(hidden, output)
        );

        // 2) Datos de entrada y listas para graficar
        double[][] inputs = {{0,0},{0,1},{1,0},{1,1}};
        List<Double> xData = new ArrayList<>(), yData = new ArrayList<>(), zData = new ArrayList<>();

        for (double[] in : inputs) {
            Signal s = new Signal();
            s.add(in[0]);
            s.add(in[1]);
            double raw = mlp.predict(s).getValues().get(0);
            xData.add(in[0]);
            yData.add(in[1]);
            zData.add(raw);
            System.out.printf("Input [%d, %d] -> raw=%.4f%n",
                (int)in[0], (int)in[1], raw);
        }

        // 3) Creamos un scatter chart, coloreando por valor crudo
        XYChart chart = new XYChartBuilder()
            .width(400).height(400)
            .title("MLP XOR Output (raw values)")
            .xAxisTitle("x1")
            .yAxisTitle("x2")
            .build();

        // Dos series: raw <0.5 y raw >=0.5, pero podemos usar degradado RGB
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();
        for (int i = 0; i < zData.size(); i++) {
            if (zData.get(i) < 0.5) {
                x0.add(xData.get(i));
                y0.add(yData.get(i));
            } else {
                x1.add(xData.get(i));
                y1.add(yData.get(i));
            }
        }

        XYSeries series0 = chart.addSeries("raw<0.5", x0, y0);
        series0.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        series0.setMarker(SeriesMarkers.CIRCLE);

        XYSeries series1 = chart.addSeries("raw≥0.5", x1, y1);
        series1.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        series1.setMarker(SeriesMarkers.CROSS);

        // 4) Mostrar chart
        new SwingWrapper<>(chart).displayChart();
    }
}
