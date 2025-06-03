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
import com.merlab.signals.plot.LogicGateScatterExample; // reutilizamos para plotting
import com.merlab.signals.plot.ChartType;
import com.merlab.signals.plot.SignalPlotter;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;
import org.knowm.xchart.style.Styler.ChartTheme;
import com.merlab.signals.nn.processor.ActivationFunctions;

import java.util.List;

/**
 * MLPClassifyExample10:
 * - Dataset de círculos concéntricos
 * - MLP: [2 → 8 ReLU → 2 Softmax]
 * - Entrenamiento backprop, logging final con ModelReporter
 */
public class MLPClassifyExample10 {

    public static void main(String[] args) {
        // Parámetros
        int    nSamples     = 500;
        double rInt         = 1.0;
        double rExt         = 2.0;
        int    hiddenNeurons= 8;
        int    epochs       = 2000;
        double learningRate = 0.01;

        // 1) Generar dataset y construir DataSet con dos salidas one-hot
        double[][] raw = SyntheticCirclesDataset.generate(nSamples, rInt, rExt);
        // DataSetBuilder asume última columna es label escalar,
        // así que convertimos label escalar [0,1] → target one-hot [1,0] o [0,1]:
        DataSet ds = DataSetBuilder.fromArray(
            convertToOneHot(raw), 
            2  // numInputs = 2 (x,y)
        );

        // 2) Definir MLP
        Layer hidden = initLayer(hiddenNeurons, 2, ActivationFunctions.RELU);
        //Layer output = initLayer(2, hiddenNeurons, ActivationFunctions.SOFTMAX);
        Layer output = initLayer(2, hiddenNeurons, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor mlp = 
            new MultiLayerPerceptronProcessor(List.of(hidden, output));

        // 3) Entrenar con backprop
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        MultiLayerPerceptronProcessor trained = 
            trainer.train(mlp, ds, epochs, learningRate);

        // 4) Evaluar accuracy
        int correct = 0;
        for (int i = 0; i < nSamples; i++) {
            Signal in = ds.getInputs().get(i);
            int trueLabel = (int) raw[i][2];
            Signal out = trained.predict(in);
            int predLabel = out.getValues().get(0) > out.getValues().get(1) ? 0 : 1;
            if (predLabel == trueLabel) correct++;
        }
        double accuracy = 100.0 * correct / nSamples;

        // 5) Graficar puntos y frontera (reutilizando tu LogicGateScatterExample)
        LogicGateScatterExample.plot(
            "MLP Clasificación: Círculos", 
            raw, trained
        );

        // 6) Reporte de modelo 
        ModelInfo info = new ModelInfo.Builder("MLP Clasificador Círculos")
            .addLayer(2, hiddenNeurons, "ReLU")
            .addLayer(hiddenNeurons, 2,    "Softmax")
            .epochs(epochs)
            .learningRate(learningRate)
            .accuracy(accuracy)
            .build();
        ModelReporter.report(info);
    }

    // convierte raw [x,y,label] → filas [x,y, t0, t1]
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

    private static Layer initLayer(int neurons, int inputs, com.merlab.signals.nn.processor.ActivationFunction act) {
        java.util.Random rnd = new java.util.Random(0);
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = (rnd.nextDouble()*2-1)*0.1;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble()*2-1)*0.1;
            }
        }
        return new Layer(w, b, act);
    }
    

}
