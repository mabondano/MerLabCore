package com.merlab.signals.nn.trainer;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;

import java.util.List;

/**
 * Implementación de entrenamiento por back-propagation
 * para un modelo de regresión logística (una capa + sigmoide).
 */
public class BackpropLogisticTrainer implements LogisticTrainer {

    @Override
    public LogisticRegressionProcessor train(
        LogisticRegressionProcessor initial,
        DataSet data,
        int epochs,
        double learningRate
    ) {
        // Clona el procesador para no modificar el original
        LogisticRegressionProcessor model = initial.copy();
        List<Signal> inputs  = data.getInputs();
        List<Signal> targets = data.getTargets();
        int n = inputs.size();

        for (int e = 0; e < epochs; e++) {
            // Para cada muestra
            for (int i = 0; i < n; i++) {
                double[] x = inputs.get(i).getValues().stream()
                                   .mapToDouble(d -> d).toArray();
                double  y = targets.get(i).getValues().get(0);

                // 1) Forward: predicción p = σ(w·x + b)
                double p = model.predict(inputs.get(i)).getValues().get(0);

                // 2) Error y gradiente δ = (p - y)
                double delta = p - y;

                // 3) Actualizar pesos y bias:
                //    w_j ← w_j − lr * δ * x_j
                //    b   ← b   − lr * δ
                double[] w = model.getWeights();
                for (int j = 0; j < w.length; j++) {
                    w[j] -= learningRate * delta * x[j];
                }
                model.setBias(model.getBias() - learningRate * delta);
            }
        }
        return model;
    }
}