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
 * MLPClassifyExample11:
 * - Dataset de círculos concéntricos.
 * - MLP: [2 → 64 ReLU → 32 ReLU → 2 identidad].
 * - Entrenamiento backprop con 5 000 épocas y lr=0.005.
 * - Gráfica con frontera de decisión y puntos.
 * - Reporte por consola de arquitectura y accuracy.
 */
public class MLPClassifyExample11 {

    public static void main(String[] args) {
        // Parámetros
        int    nSamples      = 500;
        double rInt          = 1.0;
        double rExt          = 2.0;
        int    hidden1       = 64;
        int    hidden2       = 32;
        int    epochs        = 5000;
        double learningRate  = 0.005;

        // 1) Generar raw [x,y,label]
        double[][] raw = SyntheticCirclesDataset.generate(nSamples, rInt, rExt);

        // 2) Convertir a DataSet (x,y) → one-hot label (2 salidas)
        DataSet ds = DataSetBuilder.fromArray(
            convertToOneHot(raw),
            /* numInputs= */ 2
        );

        // 3) Crear MLP con dos capas ocultas
        Layer l1 = initLayer(hidden1, 2, ActivationFunctions.RELU);
        Layer l2 = initLayer(hidden2, hidden1, ActivationFunctions.RELU);
        Layer out = initLayer(2, hidden2, ActivationFunctions.IDENTITY);

        MultiLayerPerceptronProcessor mlp = 
            new MultiLayerPerceptronProcessor(List.of(l1, l2, out));

        // 4) Entrenar
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        MultiLayerPerceptronProcessor trained =
            trainer.train(mlp, ds, epochs, learningRate);

        // 5) Calcular accuracy
        int correct = 0;
        for (int i = 0; i < nSamples; i++) {
            Signal input    = ds.getInputs().get(i);
            Signal outputS  = trained.predict(input);
            double s0       = outputS.getValues().get(0);
            double s1       = outputS.getValues().get(1);
            int predLabel   = s1 > s0 ? 1 : 0;
            int trueLabel   = (int) raw[i][2];
            if (predLabel == trueLabel) correct++;
        }
        double accuracy = 100.0 * correct / nSamples;

        // 6) Graficar
        LogicGateScatterExample.plot(
            "MLP Clasificación (2 capas ocultas)",
            raw,
            trained
        );

        // 7) Reportar modelo
        ModelInfo info = new ModelInfo.Builder("MLP Clasificador Círculos v11")
            .addLayer(2, hidden1, "ReLU")
            .addLayer(hidden1, hidden2, "ReLU")
            .addLayer(hidden2, 2,       "Identity")
            .epochs(epochs)
            .learningRate(learningRate)
            .accuracy(accuracy)
            .build();
        ModelReporter.report(info);
    }

    /** convierte raw [x,y,label] → filas [x, y, t0, t1] */
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

    /** Inicializa una capa con pesos y bias pequeños aleatorios */
    private static Layer initLayer(
        int neurons,
        int inputs,
        com.merlab.signals.nn.processor.ActivationFunction act
    ) {
        java.util.Random rnd = new java.util.Random(0);
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = (rnd.nextDouble()*2 - 1)*0.1;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble()*2 - 1)*0.1;
            }
        }
        return new Layer(w, b, act);
    }
}
