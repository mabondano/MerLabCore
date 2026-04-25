package com.merlab.signals.nn.processor;

import java.util.Random;

/**
 * Dropout: en TRAIN descarta neuronas aleatoriamente; en INFERENCE las deja pasar.
 */
public class DropoutLayer extends Layer {
    private final double keepProb;
    private final Random rnd;
    private Mode mode = Mode.TRAIN;

    /**
     * @param keepProbability probabilidad de mantener cada neurona durante TRAIN.
     * @param seed            semilla para reproducibilidad.
     * @param size            número de neuronas en esta capa.
     */
    public DropoutLayer(double keepProbability, long seed, int size) {
        super(identityWeights(size), new double[size], ActivationFunctions.IDENTITY);
        this.keepProb = keepProbability;
        this.rnd      = new Random(seed);
    }

    private static double[][] identityWeights(int size) {
        double[][] w = new double[size][size];
        for (int i = 0; i < size; i++) {
            w[i][i] = 1.0;  // matriz identidad
        }
        return w;
    }

    public void setMode(Mode m) {
        this.mode = m;
    }

    @Override
    public double[] forward(double[] x) {
        double[] out = new double[x.length];
        if (mode == Mode.INFERENCE) {
            // en inferencia simplemente devuelves x sin mascaras
            return x.clone();
        }
        if (mode == Mode.TRAIN) {
            for (int i = 0; i < x.length; i++) {
                // con probabilidad keepProb mantenemos y escalamos,
                // en caso contrario dejamos 0.
                out[i] = (rnd.nextDouble() < keepProb) ? x[i] / keepProb : 0.0;
            }
        } else {
            // INFERENCE: no modificamos nada
            System.arraycopy(x, 0, out, 0, x.length);
        }
        return out;
    }
}



/*
public DropoutLayer(double keepProbability, long seed) {
    super(null, null, ActivationFunctions.IDENTITY); // pesos/bias irrelevantes
    this.keepProb = keepProbability;
    this.rnd      = new Random(seed);
}
*/
