// src/main/java/com/merlab/signals/nn/trainer/Trainer.java
package com.merlab.signals.nn.trainer;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

/**
 * Contrato genérico para entrenar cualquier NeuralNetworkProcessor
 * y devolver un modelo entrenado.
 * @param <M> tipo de procesador (MLP, logística, lineal, etc.)
 */
public interface Trainer<M extends NeuralNetworkProcessor> {
    /**
     * Entrena el modelo inicial con el DataSet dado.
     * @param initial         modelo con pesos iniciales
     * @param data            DataSet de entrenamiento
     * @param epochs          número de épocas
     * @param learningRate    tasa de aprendizaje
     * @return modelo entrenado
     */
    M train(M initial, DataSet data, int epochs, double learningRate);
}
