package com.merlab.signals.nn.trainer;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.LayerLogisticRegressionProcessor;

import java.util.List;

/**
 * Backprop trainer for a layer-based logistic regression processor.
 */
public class LayerBackpropLogisticTrainer implements LayerLogisticTrainer {

    @Override
    public LayerLogisticRegressionProcessor train(
            LayerLogisticRegressionProcessor initial,
            DataSet data,
            int epochs,
            double learningRate
    ) {
        LayerLogisticRegressionProcessor model = initial.copy();

        Layer layer = model.getLayer();
        double[][] weights = layer.getWeights();
        double[] biases = layer.getBiases();

        List<Signal> inputs = data.getInputs();
        List<Signal> targets = data.getTargets();
        int nSamples = inputs.size();

        for (int e = 0; e < epochs; e++) {
            for (int i = 0; i < nSamples; i++) {
                double[] x = inputs.get(i)
                                   .getValues()
                                   .stream()
                                   .mapToDouble(d -> d)
                                   .toArray();
                double y = targets.get(i).getValues().get(0);

                double p = model.predict(inputs.get(i))
                               .getValues()
                               .get(0);

                double delta = p - y;

                for (int j = 0; j < x.length; j++) {
                    weights[0][j] -= learningRate * delta * x[j];
                }

                biases[0] -= learningRate * delta;
            }
        }

        return model;
    }
}
