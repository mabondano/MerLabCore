package com.merlab.signals.nn.trainer;

import com.merlab.signals.data.DataSet;

/**
 * Interfaz genérica para entrenar cualquier procesador de redes neuronales.
 * @param <M> el tipo de procesador (por ejemplo MultiLayerPerceptronProcessor,
 *           LogisticRegressionProcessor, etc.)
 */
public interface Trainer2<M> {
    /**
     * Entrena el modelo inicial con el DataSet dado.
     * @param initial       modelo con pesos iniciales (o parcialmente entrenado)
     * @param data          DataSet de entrenamiento
     * @param epochs        número de épocas
     * @param learningRate  tasa de aprendizaje
     * @return              modelo entrenado
     */
    M train(M initial, DataSet data, int epochs, double learningRate);
}
