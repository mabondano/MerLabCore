package com.merlab.nn.examples;


import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataLoaderConfig;
import com.merlab.signals.data.DataLoaderFactory;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetIO;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;

/**
 * MLPClassifyExample:
 * - Genera un dataset de círculos concéntricos
 * - Define un MLP de 3 capas: 4 neuronas ReLU + 2 neuronas Softmax
 * - Grafica el scatter de clasificación
 */
public class MLPClassifyExample {

    public static void main(String[] args) throws Exception {
        // 1) Cargar dataset sintético
        DataLoaderConfig cfg = new DataLoaderConfig();
        cfg.setSyntheticN(300);
        cfg.setSyntheticRInt(1.0);
        cfg.setSyntheticRExt(2.5);
        DataLoader loader = DataLoaderFactory.create(
            DataLoaderFactory.Type.SYNTHETIC_CIRCLES, cfg
        );
        DataSet ds = loader.load();

        // 2) Definir MLP
        List<Layer> layers = List.of(
            // Capa oculta: 4 neuronas, ReLU
            new Layer(
                new double[][] {
                    { 1.0, -1.0 },
                    { 1.0,  1.0 },
                    {-1.0,  1.0 },
                    { 1.0,  2.0 }
                },
                new double[] { 0.0, 1.0, 1.0, -1.0 },
                ActivationFunctions.RELU
            ),
            // Capa de salida: 2 neuronas, Softmax (se tratará como sigmoide doble)
            new Layer(
                new double[][] {
                    { 1.5, -2.0,  1.0, 0.5 },
                    {-1.0,  2.0, -0.5, 1.0 }
                },
                new double[] { 0.0, 0.5 },
                ActivationFunctions.SIGMOID  // simulamos softmax con sigmoid en este ejemplo
            )
        );
        NeuralNetworkProcessor mlp = new MultiLayerPerceptronProcessor(layers);

        // 3) Preparar series para scatter
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();

        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal in = ds.getInputs().get(i);
            double xi = in.getValues().get(0);
            double yi = in.getValues().get(1);
            double raw0 = mlp.predict(in).getValues().get(0);
            double raw1 = mlp.predict(in).getValues().get(1);
            // asignamos clase según la mayor de las dos salidas
            if (raw0 >= raw1) {
                x0.add(xi); y0.add(yi);
            } else {
                x1.add(xi); y1.add(yi);
            }
        }

        // 4) Graficar con XChart
        XYChart chart = new XYChartBuilder()
            .width(500).height(400)
            .title("MLP Clasificación: Círculos")
            .xAxisTitle("x").yAxisTitle("y")
            .build();

        XYSeries series0 = chart.addSeries("Clase 0", x0, y0);
        series0.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        series0.setMarker(SeriesMarkers.CIRCLE);

        XYSeries series1 = chart.addSeries("Clase 1", x1, y1);
        series1.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        series1.setMarker(SeriesMarkers.CROSS);

        new SwingWrapper<>(chart).displayChart();
    }
}
