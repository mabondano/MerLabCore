package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MLPRegressionExample3:
 * - Dataset sintético de (x, sin(x)), con x en [0, 2π]
 * - MLP de 3 capas: 10 neuronas ReLU + 1 neurona identidad
 * - Pesos y biases inicializados aleatoriamente en [-0.1, 0.1]
 */
public class MLPRegressionExample3 {

    public static void main(String[] args) {
        // 1) Generar dataset de un solo feature x
        int n = 100;
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double x = 2 * Math.PI * i / (n - 1);
            double y = Math.sin(x);

            Signal in = new Signal();
            in.add(x);
            inputs.add(in);

            Signal out = new Signal();
            out.add(y);
            targets.add(out);
        }
        DataSet ds = new DataSet(inputs, targets);

        // 2) Inicializar pesos y biases aleatorios pequeños
        Random rnd = new Random(12345);
        double[][] wHidden = new double[10][1];
        double[]   bHidden = new double[10];
        for (int i = 0; i < 10; i++) {
            bHidden[i] = (rnd.nextDouble() * 2 - 1) * 0.1;        // en [-0.1,0.1]
            wHidden[i][0] = (rnd.nextDouble() * 2 - 1) * 0.1;    // en [-0.1,0.1]
        }
        double[][] wOutput = new double[1][10];
        double[]   bOutput = new double[1];
        for (int j = 0; j < 10; j++) {
            wOutput[0][j] = (rnd.nextDouble() * 2 - 1) * 0.1;
        }
        bOutput[0] = (rnd.nextDouble() * 2 - 1) * 0.1;

        // 3) Construir las capas
        Layer hidden = new Layer(wHidden, bHidden, ActivationFunctions.RELU);
        Layer output = new Layer(wOutput, bOutput, ActivationFunctions.IDENTITY);

        NeuralNetworkProcessor mlp =
            new MultiLayerPerceptronProcessor(List.of(hidden, output));

        // 4) Predecir y mostrar primeros valores
        Signal real = new Signal();
        Signal pred = new Signal();
        for (Signal in : ds.getInputs()) {
            double x  = in.getValues().get(0);
            real.add(Math.sin(x));
            pred.add(mlp.predict(in).getValues().get(0));
        }

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
