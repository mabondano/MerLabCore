package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.data.synthetic.SyntheticCirclesDataset;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import com.merlab.signals.plot.LogicGateScatterExample;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;

import java.util.List;

/**
 * MLPClassifyExample11_WithLogging:
 * Igual que v11, pero con feedback de accuracy durante el entrenamiento.
 */
public class MLPClassifyExample11_WithLogging {

    public static void main(String[] args) {
        // Parámetros
        int    nSamples      = 500;
        double rInt          = 1.0;
        double rExt          = 2.0;
        int    hidden1       = 64;
        int    hidden2       = 32;
        int    epochs        = 5000;
        double learningRate  = 0.005;
        int    logInterval   = 500;  // cada cuántas épocas imprimimos

        // 1) Dataset
        double[][] raw = SyntheticCirclesDataset.generate(nSamples, rInt, rExt);
        DataSet ds = DataSetBuilder.fromArray(convertToOneHot(raw), 2);

        // 2) Arquitectura
        Layer l1 = initLayer(hidden1, 2, ActivationFunctions.RELU);
        Layer l2 = initLayer(hidden2, hidden1, ActivationFunctions.RELU);
        Layer out = initLayer(2, hidden2, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor mlp =
            new MultiLayerPerceptronProcessor(List.of(l1, l2, out));

        // 3) Entrenador
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        MultiLayerPerceptronProcessor current = mlp;

        // 4) Bucle de entrenamiento 1 época a la vez + logging
        for (int epoch = 1; epoch <= epochs; epoch++) {
            // avanzamos 1 época
            current = trainer.train(current, ds, 1, learningRate);

            // logging
            if (epoch % logInterval == 0 || epoch == 1 || epoch == epochs) {
                double acc = evaluateAccuracy(current, raw, ds);
                System.out.printf("Epoch %4d/%d  lr=%.4f  Acc=%.2f%%%n",
                                  epoch, epochs, learningRate, acc);
            }
        }

        // 5) Graficar con la frontera y los puntos
        LogicGateScatterExample.plot(
            "MLP Clasificación con Logging",
            raw,
            current
        );

        // 6) Reportar metadata y accuracy final
        double finalAcc = evaluateAccuracy(current, raw, ds);
        ModelInfo info = new ModelInfo.Builder("MLP Clasificador Círculos v11+Log")
            .addLayer(2, hidden1, "ReLU")
            .addLayer(hidden1, hidden2, "ReLU")
            .addLayer(hidden2, 2,       "Identity")
            .epochs(epochs)
            .learningRate(learningRate)
            .accuracy(finalAcc)
            .build();
        ModelReporter.report(info);
    }

    private static double evaluateAccuracy(MultiLayerPerceptronProcessor mlp,
                                           double[][] raw,
                                           DataSet ds) {
        int correct = 0;
        for (int i = 0; i < raw.length; i++) {
            Signal in = ds.getInputs().get(i);
            Signal out = mlp.predict(in);
            double s0 = out.getValues().get(0);
            double s1 = out.getValues().get(1);
            int pred = s1 > s0 ? 1 : 0;
            int truth = (int) raw[i][2];
            if (pred == truth) correct++;
        }
        return 100.0 * correct / raw.length;
    }

    private static double[][] convertToOneHot(double[][] raw) {
        double[][] one = new double[raw.length][4];
        for (int i = 0; i < raw.length; i++) {
            one[i][0] = raw[i][0];
            one[i][1] = raw[i][1];
            if ((int) raw[i][2] == 0) {
                one[i][2] = 1; one[i][3] = 0;
            } else {
                one[i][2] = 0; one[i][3] = 1;
            }
        }
        return one;
    }

    private static Layer initLayer(int neurons,
                                   int inputs,
                                   com.merlab.signals.nn.processor.ActivationFunction act) {
        java.util.Random rnd = new java.util.Random(0);
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
