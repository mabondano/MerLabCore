
package com.merlab.signals.nn.trainer;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

import java.nio.file.Path;
import java.util.List;

/**
 * Define el contrato para entrenar un modelo y obtener un NeuralNetworkProcessor
 * que pueda ejecutarse luego en producción.
 */
public interface ModelTrainer {

    /**
     * Entrena un modelo con los datos y devuelve un procesador de inferencia.
     *
     * @param inputs          lista de señales de entrada (vectores de features)
     * @param targets         lista de señales objetivo (por ejemplo, una sola dimensión)
     * @param modelOutputPath ruta donde guardar los pesos/coeficientes del modelo
     * @return un NeuralNetworkProcessor para inferencia
     */
    NeuralNetworkProcessor train(
        List<Signal> inputs,
        List<Signal> targets,
        Path modelOutputPath
    );
}
