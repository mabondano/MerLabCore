package com.merlab.signals.nn.processor;

import java.util.Objects;
import java.util.stream.DoubleStream;

import com.merlab.signals.core.Signal;


/**
 * Regresión logística simple: una capa con activación sigmoide.
 */

public class SimpleLogisticRegressionProcessor implements NeuralNetworkProcessor {
    private final Layer layer;

    public SimpleLogisticRegressionProcessor(Layer layer) {
        this.layer = Objects.requireNonNull(layer);
    }
    
    /**
     * Devuelve la capa interna para poder leer/modificar pesos y bias.
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * Crea un clon independiente de este procesador (copia los pesos y bias).
     */
    public SimpleLogisticRegressionProcessor copy() {
        // Creamos un nuevo arreglo de pesos idéntico
        double[][] wOrig = layer.getWeights();
        int       neurons = wOrig.length;         // debe ser 1
        int       inputs  = wOrig[0].length;      // número de features

        // Clonamos w en un nuevo arreglo
        double[][] wClone = new double[neurons][inputs];
        for (int i = 0; i < neurons; i++) {
            System.arraycopy(wOrig[i], 0, wClone[i], 0, inputs);
        }

        // Clonamos bias
        double[] bOrig = layer.getBiases();
        double[] bClone = new double[neurons];
        System.arraycopy(bOrig, 0, bClone, 0, neurons);

        // Reutilizamos la misma función de activación que tenía la capa original
        ActivationFunction act = layer.getActivationFunction();

        // Creamos una nueva capa con los mismos pesos, bias y activación
        Layer layerCopy = new Layer(wClone, bClone, act);

        // Devolvemos un nuevo SimpleLogisticRegressionProcessor
        return new SimpleLogisticRegressionProcessor(layerCopy);
    }
    /**
     * @param features Signal con N features
     * @return Signal con un único valor en [0,1]: probabilidad de clase “1”
     */
    @Override
    public Signal predict(Signal features) {
        // Convertir Signal a array
        double[] x = features.getValues().stream().flatMapToDouble(DoubleStream::of).toArray();
        // Forward por la capa
        double[] z = layer.forward(x);
        // Devuelve probabilidad
        Signal out = new Signal();
        out.add(z[0]);  
        return out;
    }
    
    /** Clona este procesador (pesos y bias). */
    /*
    public SimpleLogisticRegressionProcessor copy() {
        return new SimpleLogisticRegressionProcessor(this.layer);
    }
    */
    
}
