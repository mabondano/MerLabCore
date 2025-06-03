package com.merlab.signals.nn.trainer;

import java.util.List;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;

/**
 * Backprop para regresión logística (una sola capa sigmoide).
 * Ahora esta clase implementa la nueva interfaz genérica Trainer<LogisticRegressionProcessor>.
 */
public class BackpropLogisticTrainer2 implements Trainer2<LogisticRegressionProcessor> {

    @Override
    public LogisticRegressionProcessor train(
        LogisticRegressionProcessor model,
        DataSet data,
        int epochs,
        double learningRate
    ) {
        // Clonamos el procesador para no modificar el original
        LogisticRegressionProcessor cloned = model.copy();

        int N = data.getInputs().size();
        for (int e = 0; e < epochs; e++) {
            for (int i = 0; i < N; i++) {
                Signal xi = data.getInputs().get(i);
                double yi = data.getTargets().get(i).getValues().get(0);

                // 1) Forward: p = σ(w·x + b)
                double p = cloned.predict(xi).getValues().get(0);

                // 2) Gradiente (cross‐entropy): δ = p − y
                double delta = p - yi;

                // 3) Actualizar pesos: w_j := w_j − lr * δ * x_j
                double[] w = cloned.getWeights();
                for (int j = 0; j < w.length; j++) {
                    double xj = xi.getValues().get(j);
                    w[j] -= learningRate * delta * xj;
                }

                // 4) Actualizar bias: b := b − lr * δ
                cloned.setBias(cloned.getBias() - learningRate * delta);
            }
        }
        return cloned;
    }
}
