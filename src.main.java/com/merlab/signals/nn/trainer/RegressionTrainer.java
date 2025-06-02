// src/main/java/com/merlab/signals/nn/trainer/RegressionTrainer.java
package com.merlab.signals.nn.trainer;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.model.RegressionModel;

import java.nio.file.Path;
import java.util.List;

/**
 * Contrato para entrenadores que devuelven un RegressionModel.
 */
public interface RegressionTrainer {
    RegressionModel train(
        List<Signal> inputs,
        List<Signal> targets,
        Path modelOutputPath
    );
}
