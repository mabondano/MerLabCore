package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;

import java.util.List;
import java.util.stream.IntStream;

/**
 * MLPRegressionExample5:
 * - Usa DataSetBuilder.fromArray(raw, numInputs)
 *   para crear el DataSet de (x, sin(x)).
 */
public class MLPRegressionExample5 {

    public static void main(String[] args) {
        // 1) Generar raw array [x, sin(x)]
        int n = 100;
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x = 2 * Math.PI * i / (n - 1);
            raw[i][0] = x;       // input
            raw[i][1] = Math.sin(x); // target
        }

        // 2) Construir DataSet usando numInputs=1
        DataSet ds = DataSetBuilder.fromArray(raw, 1);

        // 3) Definir MLP (idéntico al ejemplo 3)
        double[][] wH = new double[10][1];
        double[]   bH = new double[10];
        double[][] wO = new double[1][10];
        double[]   bO = new double[1];
        
        // Inicialización dummy (puedes reutilizar la aleatoria)
        for (int i = 0; i < 10; i++) {
            wH[i][0] = 0.1 * (i+1);
            bH[i]     = 0.05;
            wO[0][i]  = 0.1;
        }
        bO[0] = 0.0;

        Layer hidden = new Layer(wH, bH, ActivationFunctions.RELU);
        Layer output = new Layer(wO, bO, ActivationFunctions.IDENTITY);
        NeuralNetworkProcessor mlp =
            new MultiLayerPerceptronProcessor(List.of(hidden, output));

        // 4) Predecir e imprimir primeros valores
        List<Signal> inputs  = ds.getInputs();
        List<Signal> targets = ds.getTargets();
        for (int i = 0; i < 5; i++) {
            double x    = inputs.get(i).getValues().get(0);
            double real = targets.get(i).getValues().get(0);
            double pred = mlp.predict(inputs.get(i)).getValues().get(0);
            System.out.printf("x=%.3f → real=%.3f, pred=%.3f%n", x, real, pred);
        }
        
    }
}
