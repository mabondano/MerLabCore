package com.merlab.signals.nn.processor;

import java.util.Arrays;

/**
 * Normalización por batch: 
 *  - en TRAIN calcula μ/σ de cada dimensión y normaliza
 *  - en INFERENCE aplica escala y desplazamiento aprendidos (γ, β fijos)
 */
/**
 * Normalización por batch simplificada.
 * En TRAIN actualiza media/var y normaliza.
 * En INFERENCE aplica con estadísticas “running”.
 */
public class BatchNormLayer extends Layer {
    private final int size;
    private final double eps = 1e-5;
    private final double[] runningMean, runningVar;
    private Mode mode = Mode.TRAIN;

    public BatchNormLayer(int size) {
        super(identityWeights(size), new double[size], ActivationFunctions.IDENTITY);
        this.size = size;
        this.runningMean = new double[size];
        this.runningVar  = new double[size];
        // Inicializar runningVar con ε para no dividir por cero
        Arrays.fill(this.runningVar, eps);
    }

    public void setMode(Mode m) {
        this.mode = m;
    }

    @Override
    public double[] forward(double[] x) {
        double[] out = new double[size];
        if (mode == Mode.TRAIN) {
            // 1) Calcular media (batch de 1)
            double[] mu = new double[size];
            for (int i = 0; i < size; i++) {
                mu[i] = x[i];
                runningMean[i] = mu[i];
            }
            // 2) Calcular “varianza + eps”
            double[] varp = new double[size];
            for (int i = 0; i < size; i++) {
                varp[i] = eps;          // var=0 + eps
                runningVar[i] = varp[i];
            }
            // 3) Normalizar
            for (int i = 0; i < size; i++) {
                out[i] = (x[i] - mu[i]) / Math.sqrt(varp[i] + eps);
            }
        } else {
            // INFERENCE: usar runningMean y runningVar
            for (int i = 0; i < size; i++) {
                out[i] = (x[i] - runningMean[i]) / Math.sqrt(runningVar[i] + eps);
            }
        }
        return out;
    }

    private static double[][] identityWeights(int size) {
        double[][] w = new double[size][size];
        for (int i = 0; i < size; i++) w[i][i] = 1.0;
        return w;
    }
}