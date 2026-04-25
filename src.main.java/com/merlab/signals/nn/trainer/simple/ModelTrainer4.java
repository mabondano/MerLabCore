package com.merlab.signals.nn.trainer.simple;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

/**
 * Contrato genérico para entrenar cualquier procesador de señales.
 *
 * @param <P> Tipo de procesador que entrena (p.ej. MultiLayerPerceptronProcessor,
 *            LogisticRegressionProcessor, KNearestProcessor, etc.)
 */
public interface ModelTrainer4<P> {
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

    /** 
     * --- FIRMA ANTIGUA, para compatibilidad ---
     * Entrena con un único batch y guarda el modelo en disco.
     * @deprecated usar {@link #train(NeuralNetworkProcessor, DataSet, int, double)}
     *
    @Deprecated
    NeuralNetworkProcessor train(
        List<Signal> inputs,
        List<Signal> targets,
        Path modelOutputPath
    );

    /**
     * --- NUEVA FIRMA, para pipelines iterativos ---
     * Toma un modelo inicial (p. ej. un MLP con pesos aleatorios), 
     * lo entrena sobre un DataSet completo (o un mini‐batch) 
     * durante N epochs con learning rate y devuelve
     * un procesador listo para inferencia.
     *  
     * Por defecto lanza UnsupportedOperationException, 
     * así no obliga a los trainers viejos a implementarla.
     *
    default NeuralNetworkProcessor train(
        NeuralNetworkProcessor model,
        DataSet batch,
        int epochs,
        double lr
    ) {
        throw new UnsupportedOperationException(
            "Este trainer no soporta la firma moderna de entrenar por época"
        );
    }
    */

