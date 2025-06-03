package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.data.DataSetIO;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * MLPRegressionExample:
 * - Dataset sintético de (x, sin(x)), con x en [0, 2π]
 * - MLP de 3 capas: 10 neuronas ReLU + 1 neurona identidad
 * - Grafica real vs predicción usando SignalPlotter.plotSignal2
 */
public class MLPRegressionExample2 {

    public static void main(String[] args) throws Exception {
        // 1) Generar dataset de un solo feature x
        int n = 100;
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        Signal real = new Signal();
        for (int i = 0; i < n; i++) {
            double x = 2 * Math.PI * i / (n - 1);
            double y = Math.sin(x);

            Signal in = new Signal();
            in.add(x);
            inputs.add(in);

            Signal out = new Signal();
            out.add(y);
            targets.add(out);

            real.add(y);
        }
        DataSet ds = new DataSet(inputs, targets);

        // 2) Definir MLP
        Layer hidden = new Layer(
            new double[10][1],    // 10 neuronas, cada una recibe 1 input
            new double[10],       // biases
            ActivationFunctions.RELU
        );
        Layer output = new Layer(
            new double[][] { {1,1,1,1,1,1,1,1,1,1} },  // 1 neurona, recibe 10 inputs
            new double[] { 0.0 },
            ActivationFunctions.IDENTITY
        );
        NeuralNetworkProcessor mlp =
            new MultiLayerPerceptronProcessor(List.of(hidden, output));

        // 3) Predecir y almacenar predicciones
        Signal pred = new Signal();
        for (Signal in : ds.getInputs()) {
            pred.add( mlp.predict(in).getValues().get(0) );
        }

        // 4) Guardar CSV y graficar
        DataSetIO.saveToCsv(ds, Path.of("out/sin_dataset.csv"));
        // Mostrar primeros 5 valores reales vs predichos
        System.out.println("Real (5): " + real.getValues().subList(0,5));
        System.out.println("Pred (5): " + pred.getValues().subList(0,5));

        // Graficar línea real vs predicción
        com.merlab.signals.plot.SignalPlotter.plotSignal2(
            "Regresión sin(x): Real",
            real,
            org.knowm.xchart.style.Styler.ChartTheme.XChart,
            com.merlab.signals.plot.ChartType.LINE
        );
        com.merlab.signals.plot.SignalPlotter.plotSignal2(
            "Regresión sin(x): Predicción",
            pred,
            org.knowm.xchart.style.Styler.ChartTheme.XChart,
            com.merlab.signals.plot.ChartType.LINE
        );
    }
}
