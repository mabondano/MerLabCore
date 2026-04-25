package com.merlab.signals.nn.processor;

import com.merlab.signals.core.Signal;
import java.util.List;
import java.util.Objects;

/**
 * Perceptrón simple con función de activación configurable.
 */
public class ConfigPerceptronProcessor implements NeuralNetworkProcessor {
    private final double[] weights;
    private final double bias;
    private ActivationFunction activation;

    /**
     * @param weights     vector de pesos
     * @param bias        sesgo
     * @param activation  función de activación a aplicar
     */
    public ConfigPerceptronProcessor(double[] weights, double bias, ActivationFunction activation) {
        this.weights    = weights;
        this.bias       = bias;
        this.activation = activation;
    }
    
    public void setActivation(ActivationFunction activation) {
        this.activation = Objects.requireNonNull(activation);
    }


    @Override
    public Signal predict(Signal features) {
        List<Double> vals = features.getValues();
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * vals.get(i);
        }
        // aplicamos la función seleccionada
        double out = activation.apply(sum);

        Signal output = new Signal();
        output.add(out);
        return output;
    }
}