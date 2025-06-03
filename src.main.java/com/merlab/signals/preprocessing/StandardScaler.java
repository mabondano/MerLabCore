package com.merlab.signals.preprocessing;

import com.merlab.signals.core.Signal;

import java.util.List;

/**
 * Normaliza cada dimensión de un conjunto de señales (media 0, varianza 1)
 * y permite deshacer la transformación.
 */
public class StandardScaler {

    private double[] means;
    private double[] stds;

    /** 
     * Ajusta el scaler calculando media y desviación de cada dimensión. 
     * @param signals lista de Signals, todas de igual longitud.
     */
    public void fit(List<Signal> signals) {
        if (signals.isEmpty()) {
            throw new IllegalArgumentException("No hay señales para ajustar StandardScaler");
        }
        int dim = signals.get(0).size();
        int n   = signals.size();

        means = new double[dim];
        stds  = new double[dim];

        // 1) Sumar todos los valores
        for (Signal s : signals) {
            if (s.size() != dim) {
                throw new IllegalArgumentException("Señales de distinto tamaño en fit()");
            }
            for (int i = 0; i < dim; i++) {
                means[i] += s.getValues().get(i);
            }
        }
        for (int i = 0; i < dim; i++) {
            means[i] /= n;
        }

        // 2) Varianza
        for (Signal s : signals) {
            for (int i = 0; i < dim; i++) {
                double diff = s.getValues().get(i) - means[i];
                stds[i]    += diff * diff;
            }
        }
        for (int i = 0; i < dim; i++) {
            stds[i] = Math.sqrt(stds[i] / n);
            // Evitar división por cero:
            if (stds[i] == 0) {
                stds[i] = 1e-8;
            }
        }
    }

    /**
     * Transforma (normaliza) una lista de señales.
     * @param signals lista original
     * @return lista nueva con señales normalizadas
     */
    public List<Signal> transform(List<Signal> signals) {
        return signals.stream()
                      .map(this::transformOne)
                      .toList();
    }

    /**
     * Deshace la transformación sobre una lista de señales normalizadas.
     * @param signals normalizadas
     * @return lista con valores en la escala original
     */
    public List<Signal> inverseTransform(List<Signal> signals) {
        return signals.stream()
                      .map(this::inverseTransformOne)
                      .toList();
    }

    private Signal transformOne(Signal s) {
        if (s.size() != means.length) {
            throw new IllegalArgumentException("Dimensión incorrecta en transformOne()");
        }
        Signal out = new Signal();
        for (int i = 0; i < means.length; i++) {
            double v = (s.getValues().get(i) - means[i]) / stds[i];
            out.add(v);
        }
        return out;
    }

    private Signal inverseTransformOne(Signal s) {
        if (s.size() != means.length) {
            throw new IllegalArgumentException("Dimensión incorrecta en inverseTransformOne()");
        }
        Signal out = new Signal();
        for (int i = 0; i < means.length; i++) {
            double v = s.getValues().get(i) * stds[i] + means[i];
            out.add(v);
        }
        return out;
    }
}
