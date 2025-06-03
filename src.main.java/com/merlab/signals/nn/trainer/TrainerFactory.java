package com.merlab.signals.nn.trainer;

import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

@SuppressWarnings("unchecked")
public class TrainerFactory {
    public enum Algorithm { MLP_BACKPROP, LOGISTIC_REGRESSION, KNN /*…*/ }

    /**
     * Crea un Trainer especializado según el algoritmo deseado.
     * @param algo el algoritmo
     * @param <M>  el tipo de modelo que produce el Trainer
     * @return     un Trainer<M>
     */
    public static <M extends NeuralNetworkProcessor> Trainer<M> create(Algorithm algo) {
    //public static <M extends NeuralNetworkProcessor> Trainer<M> create(Algorithm algo) {
        switch (algo) {
            case MLP_BACKPROP:
                // BackpropMLPTrainer implementa Trainer<MultiLayerPerceptronProcessor>
                return (Trainer<M>) new BackpropMLPTrainer();

            case LOGISTIC_REGRESSION:
                // LogisticRegressionTrainer implementa Trainer<LogisticRegressionModel>
                return (Trainer<M>) new BackpropLogisticTrainer();

            case KNN:
                // KNearestTrainer implementa Trainer<KNearestModel>
                return (Trainer<M>) new KNearestTrainer();

            // … otros casos …

            default:
                throw new IllegalArgumentException("Algoritmo no soportado: " + algo);
        }
    }
    
    /**
     * NUEVO MÉTODO que sabe de antemano que, para regresión logística,
     * el Trainer que va a devolver siempre es Trainer<LogisticRegressionProcessor>.
     */
    public static Trainer<LogisticRegressionProcessor> createLogisticTrainer() {
        return (Trainer<LogisticRegressionProcessor>) new BackpropLogisticTrainer();
    }
}
