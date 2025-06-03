package com.merlab.signals.nn.trainer.simple;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

import java.nio.file.Path;
import java.util.List;


/**
 * Firma original para entrenar un modelo y obtener un NeuralNetworkProcessor.
 */
public interface SimpleModelTrainer {
    /**
     * Entrena un modelo con los datos y devuelve un procesador de inferencia.
     *
     * @param inputs  lista de señales de entrada (vectores de features)
     * @param targets lista de señales objetivo
     * @param modelOutputPath ruta donde guardar los pesos/coeficientes
     * @return un NeuralNetworkProcessor para inferencia
     */
    NeuralNetworkProcessor train(
        List<Signal> inputs,
        List<Signal> targets,
        Path modelOutputPath
    );
}
