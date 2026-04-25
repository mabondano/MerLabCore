package com.merlab.signals.integration;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.*;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MLPIntegrationTest {

    /** Genera dataset sintético [xNorm, sin(x)]. */
    private DataSet makeSineData(int n) {
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x     = 2 * Math.PI * i / (n - 1);
            double xNorm = (x - Math.PI) / Math.PI;
            raw[i][0] = xNorm;
            raw[i][1] = Math.sin(x);
        }
        return DataSetBuilder.fromArray(raw, 1);
    }

    /** Entrena un MLP y retorna el MSE sobre todo el dataset. */
    private double trainAndEval(MultiLayerPerceptronProcessor mlp, DataSet ds, int epochs, double lr, int batchSize) {
    	BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        int n = ds.getInputs().size();
        List<Integer> idx = new ArrayList<>(IntStream.range(0, n).boxed().toList());
        Random rnd = new Random(0);

        for (int e = 1; e <= epochs; e++) {
            mlp.setMode(Mode.TRAIN);
            // mini-batch
            for (int i = 0; i < n; i += batchSize) {
                int end = Math.min(n, i + batchSize);
                List<Signal> inB = ds.getInputs().subList(i, end);
                List<Signal> tgB = ds.getTargets().subList(i, end);
                DataSet b = new DataSet(inB, tgB);
                mlp = trainer.train(mlp, b, 1, lr);
            }
        }

        // evaluación
        mlp.setMode(Mode.INFERENCE);
        double sumSq = 0;
        for (int i = 0; i < n; i++) {
            double yTrue = ds.getTargets().get(i).getValues().get(0);
            double yHat  = mlp.predict(ds.getInputs().get(i)).getValues().get(0);
            sumSq += Math.pow(yTrue - yHat, 2);
        }
        return sumSq / n;
    }
    
    
    /**
     * ¿Qué hace este test?
     *
     * 1. Genera 200 puntos (xNorm, sin(x)).
     * 2. Entrena Modelo Base (2 capas ReLU + salida identidad).
     * 3. Entrena Modelo Mejorado (intercalando BatchNorm + Dropout).
     * 4. Calcula el MSE sobre todo el dataset en modo INFERENCE.
     * 5. Comprueba que MSE_mejorado < MSE_base.
     */
    @Test
    public void batchNormDropoutImprovesMSE() {
        final int N = 200, EPOCHS = 2_000, BATCH = 16;
        final double LR = 0.01;

        DataSet ds = makeSineData(N);

        // --- Modelo Base ---
        Random rnd = new Random(123);
        Layer h1b = initLayer(32, 1, rnd, ActivationFunctions.RELU);
        Layer h2b = initLayer(16, 32, rnd, ActivationFunctions.RELU);
        Layer outb = initLayer(1, 16, rnd, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor baseNet =
            new MultiLayerPerceptronProcessor(List.of(h1b, h2b, outb));

        double mseBase = trainAndEval(baseNet, ds, EPOCHS, LR, BATCH);

        // --- Modelo Mejorado (BatchNorm + Dropout) ---
        rnd.setSeed(123);
        Layer h1 = initLayer(32, 1, rnd, ActivationFunctions.RELU);
        Layer bn1 = new BatchNormLayer(32);
        Layer dp1 = new DropoutLayer(0.8, 42L, 32);
        Layer h2 = initLayer(16, 32, rnd, ActivationFunctions.RELU);
        Layer bn2 = new BatchNormLayer(16);
        Layer dp2 = new DropoutLayer(0.8, 42L, 16);
        Layer out = initLayer(1, 16, rnd, ActivationFunctions.IDENTITY);

        MultiLayerPerceptronProcessor augNet =
            new MultiLayerPerceptronProcessor(List.of(h1, bn1, dp1, h2, bn2, dp2, out));

        double mseAug = trainAndEval(augNet, ds, EPOCHS, LR, BATCH);

        System.out.printf("MSE Base = %.6f,  MSE Augmented = %.6f%n", mseBase, mseAug);

        assertTrue(mseAug < mseBase,
            "Se esperaba que el MSE con BatchNorm+Dropout ("+mseAug+
            ") fuera menor que el del modelo base ("+mseBase+")."
        );
    }

    private Layer initLayer(int neurons, int inputs, Random rnd, ActivationFunction act) {
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
}

/*
¿Qué hace este test?

Genera 200 puntos (xNorm, sin(x)).

Entrena Modelo Base (2 capas ReLU + salida identidad).

Entrena Modelo Mejorado (intercalando BatchNorm + Dropout).

Calcula el MSE sobre todo el dataset en modo INFERENCE.

Comprueba que MSE_mejorado < MSE_base.
*/