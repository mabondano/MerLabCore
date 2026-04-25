package com.merlab.signals.nn.trainer;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.processor.SimpleLogisticRegressionProcessor;

import java.util.List;

/**
 * Entrenador para regresión logística de una sola capa (sigmoide).
 */
public interface SimpleLogisticTrainer {
    /**
     * @param initial       procesador inicial (con pesos/bias inicializados)
     * @param data          DataSet con inputs y targets (0/1)
     * @param epochs        número de épocas
     * @param learningRate  tasa de aprendizaje
     * @return procesador entrenado
     */
    SimpleLogisticRegressionProcessor train(
        SimpleLogisticRegressionProcessor initial,
        DataSet data,
        int epochs,
        double learningRate
    );
}
