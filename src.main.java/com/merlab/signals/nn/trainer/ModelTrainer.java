package com.merlab.signals.nn.trainer;

import com.merlab.signals.data.DataSet;


/**
 * Contrato genérico para entrenar cualquier procesador de señales.
 *
 * @param <P> Tipo de procesador que entrena (p.ej. MultiLayerPerceptronProcessor,
 *            LogisticRegressionProcessor, KNearestProcessor, etc.)
 */
public interface ModelTrainer<P> {
	    /**
	     * Entrena el procesador dado sobre un DataSet.
	     *
	     * @param model         instancia no entrenada (o entrenada parcialmente) del procesador
	     * @param data          conjunto de datos de entrenamiento
	     * @param epochs        número de epochs
	     * @param learningRate  tasa de aprendizaje (si aplica; algunos entrenadores la ignorarán)
	     * @return              el mismo procesador, ya entrenado
	     */
	    P train(P model, DataSet data, int epochs, double learningRate);
}
/*
public interface ModelTrainer {

    /**
     * Entrena un modelo con los datos y devuelve un procesador de inferencia.
     *
     * @param inputs          lista de señales de entrada (vectores de features)
     * @param targets         lista de señales objetivo (por ejemplo, una sola dimensión)
     * @param modelOutputPath ruta donde guardar los pesos/coeficientes del modelo
     * @return un NeuralNetworkProcessor para inferencia
     *
    NeuralNetworkProcessor train(
        List<Signal> inputs,
        List<Signal> targets,
        Path modelOutputPath
    );
}
*/
