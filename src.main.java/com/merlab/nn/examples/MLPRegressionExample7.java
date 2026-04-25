package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import com.merlab.signals.plot.ChartType;
import com.merlab.signals.plot.SignalPlotter;
import org.knowm.xchart.style.Styler.ChartTheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * MLPRegressionExample7:
 * - Normaliza x a [-1,1].
 * - Entrena en batches completos con backpropagation.
 * - Imprime MSE cada 1000 épocas.
 * - Grafica Real vs Predicción al final.
 */
public class MLPRegressionExample7 {

    public static void main(String[] args) throws Exception {
        // 1) Generar raw array [xNorm, sin(x)] donde xNorm ∈ [-1,1]
        int n = 100;
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x    = 2 * Math.PI * i / (n - 1);
            double xNorm = (x - Math.PI) / Math.PI;  // [0,2π] → [-1,1]
            raw[i][0] = xNorm;
            raw[i][1] = Math.sin(x);
        }
        DataSet ds = DataSetBuilder.fromArray(raw, 1);

        // 2) Definir MLP inicial con pesos aleatorios en [-0.1,0.1]
        Random rnd = new Random(123);
        double[][] wH = new double[20][1];  // aumentamos a 20 neuronas ocultas
        double[]   bH = new double[20];
        double[][] wO = new double[1][20];
        double[]   bO = new double[1];
        for (int i = 0; i < 20; i++) {
            wH[i][0] = (rnd.nextDouble() * 2 - 1) * 0.1;
            bH[i]     = (rnd.nextDouble() * 2 - 1) * 0.1;
            wO[0][i]  = (rnd.nextDouble() * 2 - 1) * 0.1;
        }
        bO[0] = (rnd.nextDouble() * 2 - 1) * 0.1;

        Layer hidden = new Layer(wH, bH, ActivationFunctions.RELU);
        Layer output = new Layer(wO, bO, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor initial =
            new MultiLayerPerceptronProcessor(List.of(hidden, output));

        // 3) Entrenar con backpropagation
        int    epochs       = 10000;
        double learningRate = 0.005;
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        MultiLayerPerceptronProcessor trained = trainer.train(initial, ds, epochs, learningRate);
            //trainer.trainWithLogging(initial, ds, epochs, learningRate, 1000);

        // 4) Preparar señales real y predicción
        Signal real = new Signal();
        Signal pred = new Signal();
        for (int i = 0; i < ds.getInputs().size(); i++) {
            double yTrue = ds.getTargets().get(i).getValues().get(0);
            double yHat  = trained.predict(ds.getInputs().get(i))
                                    .getValues().get(0);
            real.add(yTrue);
            pred.add(yHat);
        }

        // 5) Graficar ambas series en una sola ventana
        SignalPlotter.plotSignal2(
            "sin(x) Real vs Predicción",
            real,
            ChartTheme.XChart,
            ChartType.LINE
        );
        SignalPlotter.plotSignal2(
            "sin(x) Predicción",
            pred,
            ChartTheme.GGPlot2,
            ChartType.LINE
        );
    }
}
