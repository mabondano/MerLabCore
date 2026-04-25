package com.merlab.signals.nn.trainer;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;

/**
 * Interfaz para entrenar un MLP con backpropagation.
 */
public interface MLPTrainer {
    /**
     * Entrena un MLP a partir de un DataSet usando backpropagation.
     *
     * @param initialProcessor MLP con la arquitectura (capas, pesos iniciales).
     * @param data             DataSet con inputs (Signal de dimensión d) y targets (Signal de dimensión k).
     * @param epochs           número de épocas.
     * @param learningRate     tasa de aprendizaje.
     * @return un nuevo MultiLayerPerceptronProcessor con pesos entrenados.
     */
    MultiLayerPerceptronProcessor train(
        MultiLayerPerceptronProcessor initialProcessor,
        DataSet data,
        int epochs,
        double learningRate
    );
}
