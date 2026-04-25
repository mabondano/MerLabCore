package com.merlab.signals.nn.trainer;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.SimpleLogisticRegressionProcessor;

import java.util.List;

/**
 * Un trainer muy sencillo que ajusta los pesos y bias de un único neurona
 * con sigmoide por descenso de gradiente “manual”.
 */
public class SimpleBackpropLogisticTrainer implements SimpleLogisticTrainer {

    @Override
    public SimpleLogisticRegressionProcessor train(
            SimpleLogisticRegressionProcessor initial,
            DataSet data,
            int epochs,
            double learningRate
    ) {
        // 1) Clonar el procesador para no modificar el original
        SimpleLogisticRegressionProcessor model = initial.copy();

        // 2) Extraer la capa interna (una sola neurona) para actualizar pesos y bias
        Layer layer = model.getLayer();
        // Layer almacena w como double[neurones][inputs] → aquí neurons=1
        double[][] weights = layer.getWeights(); // [[w0, w1, ...]]
        double[]   biases  = layer.getBiases();  // [b]

        List<Signal> inputs  = data.getInputs();
        List<Signal> targets = data.getTargets();
        int nSamples = inputs.size();

        // 3) Loop de epochs
        for (int e = 0; e < epochs; e++) {
            // (Opcional: podrías barajar índices para stochastic)
            for (int i = 0; i < nSamples; i++) {
                // 3.1) Convertir Signal → array x[]
                double[] x = inputs.get(i)
                                   .getValues()
                                   .stream()
                                   .mapToDouble(d -> d)
                                   .toArray();
                // 3.2) Etiqueta real y (0 o 1)
                double y = targets.get(i).getValues().get(0);

                // 3.3) Forward: predicción p = σ(w·x + b)
                double p = model.predict(inputs.get(i))
                               .getValues()
                               .get(0);

                // 3.4) Calcular delta = p - y (gradiente de cross-entropy w/ sigmoid)
                double delta = p - y;

                // 3.5) Actualizar pesos w_j ← w_j - lr * delta * x_j
                for (int j = 0; j < x.length; j++) {
                    weights[0][j] -= learningRate * delta * x[j];
                }

                // 3.6) Actualizar bias b ← b - lr * delta
                biases[0] -= learningRate * delta;
            }
        }

        return model;
    }
}

