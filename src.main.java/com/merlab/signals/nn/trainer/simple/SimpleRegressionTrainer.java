// src/main/java/com/merlab/signals/nn/trainer/RegressionTrainer.java
package com.merlab.signals.nn.trainer.simple;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.model.RegressionModel;
import com.merlab.signals.nn.model.SimpleRegressionModel;

import java.nio.file.Path;
import java.util.List;

/**
 * Contrato para entrenadores que devuelven un RegressionModel.
 */
public interface SimpleRegressionTrainer {
    SimpleRegressionModel train(
        List<Signal> inputs,
        List<Signal> targets,
        Path modelOutputPath
    );
}
