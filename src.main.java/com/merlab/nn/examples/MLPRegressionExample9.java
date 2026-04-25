package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.BatchNormLayer;
import com.merlab.signals.nn.processor.DropoutLayer;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import com.merlab.signals.plot.SignalPlotter;
import com.merlab.signals.plot.ChartType;
import org.knowm.xchart.style.Styler.ChartTheme;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * MLPRegressionExample9:
 * - Dos capas ocultas (30 y 15 neuronas, ReLU) + salida (1 neurona, identidad)
 * - Mini-batch gradient descent (batchSize=16)
 * - 20 000 epochs, lr inicial 0.01, lr *=0.5 en epoch 10 000 y 15 000
 * - Imprime MSE cada 2 000 epochs
 * - Grafica sin(x) real vs predicción usando x normalizado como eje X
 */
public class MLPRegressionExample9 {

    public static void main(String[] args) throws Exception {
        // 1) Dataset sintético [xNorm, sin(x)]
        int n = 100;
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x     = 2 * Math.PI * i / (n - 1);
            double xNorm = (x - Math.PI) / Math.PI; // [-1,1]
            raw[i][0] = xNorm;
            raw[i][1] = Math.sin(x);
        }
        DataSet ds = DataSetBuilder.fromArray(raw, 1);

        // 2) Arquitectura del MLP
        Random rnd = new Random(123);
        Layer hidden1 = initLayer(30, 1, rnd, ActivationFunctions.RELU);
        Layer hidden2 = initLayer(15, 30, rnd, ActivationFunctions.RELU);
        Layer output  = initLayer(1, 15, rnd, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor mlp =
            new MultiLayerPerceptronProcessor(List.of(hidden1, hidden2, output));

        // 3) Entrenador
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        int    epochs    = 20_000;
        double lr        = 0.01;
        int    batchSize = 16;

        // 4) Indices y training loop mini-batch
        /*
        List<Integer> indices = IntStream.range(0, n)
        	    .boxed()
        	    .collect(Collectors.toCollection(ArrayList::new));
        */
        // 4) Indices y training loop mini-batch
        List<Integer> indices = new ArrayList<>(IntStream.range(0, n).boxed().toList());
        Collections.shuffle(indices, rnd);

        for (int epoch = 1; epoch <= epochs; epoch++) {
            // lr schedule
            if (epoch == 10_000 || epoch == 15_000) {
                lr *= 0.5;
            }
            Collections.shuffle(indices, rnd);
            for (int i = 0; i < n; i += batchSize) {
                int end = Math.min(i + batchSize, n);
                double[][] batch = new double[end - i][2];
                for (int j = i; j < end; j++) {
                    batch[j - i] = raw[indices.get(j)];
                }
                DataSet dsBatch = DataSetBuilder.fromArray(batch, 1);
                mlp = trainer.train(mlp, dsBatch, 1, lr);
            }
            if (epoch % 2_000 == 0) {
                double mse = computeMSE(mlp, raw);
                System.out.printf("Epoch %5d, lr=%.5f, MSE=%.6f%n", epoch, lr, mse);
            }
        }

        // 5) Preparar listas para graficar
        List<Double> xData = new ArrayList<>(n);
        List<Double> yReal = new ArrayList<>(n);
        List<Double> yPred = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            xData.add(raw[i][0]);
            yReal.add(raw[i][1]);

            // CORRECCIÓN: extraigo la señal y llamo predict sobre Signal
            Signal inputSignal = ds.getInputs().get(i);
            double yHat = mlp.predict(inputSignal).getValues().get(0);
            yPred.add(yHat);
        }

        // 6) Ordenar por xNorm 
        List<Integer> sorted = IntStream.range(0, n)
            .boxed()
            .sorted(Comparator.comparingDouble(xData::get))
            .toList();
        List<Double> xs = sorted.stream().map(xData::get).toList();
        List<Double> rs = sorted.stream().map(yReal::get).toList();
        List<Double> ps = sorted.stream().map(yPred::get).toList();

        // 7) Graficar real
        SignalPlotter.plotSignal2(
            "sin(x) Real",
            buildSignal(rs),
            ChartTheme.XChart,
            ChartType.LINE
        );
        // 8) Graficar predicción
        SignalPlotter.plotSignal2(
            "sin(x) Predicción",
            buildSignal(ps),
            ChartTheme.GGPlot2,
            ChartType.LINE
        );
    }

    private static Layer initLayer(
        int neurons,
        int inputs,
        Random rnd,
        com.merlab.signals.nn.processor.ActivationFunction act
    ) {
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = (rnd.nextDouble() * 2 - 1) * 0.1;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble() * 2 - 1) * 0.1;
            }
        }
        return new Layer(w, b, act);
    }

    private static double computeMSE(MultiLayerPerceptronProcessor mlp, double[][] raw) {
        double sum = 0;
        for (double[] row : raw) {
            double yTrue = row[1];
            double yHat  = mlp.predict(new Signal(List.of(row[0]))).getValues().get(0);
            sum += Math.pow(yTrue - yHat, 2);
        }
        return sum / raw.length;
    }

    private static Signal buildSignal(List<Double> vals) {
        Signal s = new Signal();
        vals.forEach(s::add);
        return s;
    }
}
