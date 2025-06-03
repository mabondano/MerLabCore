package com.merlab.signals.nn.trainer;

import com.merlab.signals.ml.KNearestTrainer2;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;

/**
 * Fábrica de trainers.  
 * Agregamos este método para regresión logística sin tener que castear.
 */
public class TrainerFactory2 {

    public enum Algorithm {
        MLP_BACKPROP,
        LOGISTIC_REGRESSION,
        KNN
        // …otros algoritmos…
    }

    @SuppressWarnings("unchecked")
    public static <M> Trainer2<M> create(Algorithm algo) {
        switch(algo) {
        	case KNN:
        		// este cast es seguro si en Algorithm2 defino KNN como <KNearestProcessor2>
        		return (Trainer2<M>) new KNearestTrainer2();
            case MLP_BACKPROP:
                return (Trainer2<M>) new BackpropMLPTrainer();
            case LOGISTIC_REGRESSION:
                // Este casteo funciona en ejecución **siempre que**
                // BackpropLogisticTrainer implemente Trainer<LogisticRegressionProcessor>.
                return (Trainer2<M>) new BackpropLogisticTrainer2();
            // …otros casos…
            default:
                throw new IllegalArgumentException("Algoritmo no soportado: " + algo);
        }
    }

    /**
     * Método específico para regresión logística, que devuelve
     * directamente un Trainer<LogisticRegressionProcessor> sin casteos.
     */
    public static Trainer2<LogisticRegressionProcessor> createLogisticTrainer() {
        return new BackpropLogisticTrainer2();
    }
}