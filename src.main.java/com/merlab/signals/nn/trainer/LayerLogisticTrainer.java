package com.merlab.signals.nn.trainer;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.LayerLogisticRegressionProcessor;

/**
 * Trainer for logistic regression backed by a single Layer.
 */
public interface LayerLogisticTrainer {
    LayerLogisticRegressionProcessor train(
        LayerLogisticRegressionProcessor initial,
        DataSet data,
        int epochs,
        double learningRate
    );
}
