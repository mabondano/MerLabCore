package com.merlab.signals.ml;

import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.trainer.Trainer2;

public class SVMTrainer3 implements Trainer2<SVMProcessor2> {
    private final int maxEpochs;
    private final double C;       // regularization
    private final double tol;     // tolerance for KKT
    private final double gamma;   // for RBF kernel

    public SVMTrainer3(int maxEpochs, double C, double tol, double gamma) {
        this.maxEpochs = maxEpochs;
        this.C = C;
        this.tol = tol;
        this.gamma = gamma;
    }

    @Override
    public SVMProcessor2 train(SVMProcessor2 model, DataSet data, int ignored, double ignored2) {
        int N = data.getInputs().size();
        model.setKernel(SVMProcessor2.KernelType.RBF);
        model.setGamma(gamma);

        double[] alpha = new double[N];
        double b = 0.0;
        List<double[]> X = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            double[] xi = data.getInputs().get(i).getValues().stream().mapToDouble(Double::doubleValue).toArray();
            X.add(xi);
            y.add(data.getTargets().get(i).get(0));
        }

        // Precompute kernel matrix
        double[][] K = new double[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                K[i][j] = model.kernel(X.get(i), X.get(j));

        int passes = 0;
        int maxPasses = 10; // Termina si no hay cambios significativos

        int iteration = 0;
        while (passes < maxPasses && iteration < 500) {
            int numChanged = 0;
            for (int i = 0; i < N; i++) {
                double Ei = predictRaw(alpha, y, K, b, i) - y.get(i);

                if ((y.get(i) * Ei < -tol && alpha[i] < C) || (y.get(i) * Ei > tol && alpha[i] > 0)) {
                    // Elige j al azar distinto de i
                    int j = i;
                    while (j == i)
                        j = (int) (Math.random() * N);
                    double Ej = predictRaw(alpha, y, K, b, j) - y.get(j);

                    double alphaIOld = alpha[i];
                    double alphaJOld = alpha[j];

                    double L, H;
                    if (y.get(i) != y.get(j)) {
                        L = Math.max(0, alpha[j] - alpha[i]);
                        H = Math.min(C, C + alpha[j] - alpha[i]);
                    } else {
                        L = Math.max(0, alpha[i] + alpha[j] - C);
                        H = Math.min(C, alpha[i] + alpha[j]);
                    }
                    if (L == H)
                        continue;

                    double eta = 2 * K[i][j] - K[i][i] - K[j][j];
                    if (eta >= 0)
                        continue;

                    alpha[j] -= y.get(j) * (Ei - Ej) / eta;
                    alpha[j] = clip(alpha[j], L, H);
                    if (Math.abs(alpha[j] - alphaJOld) < 1e-5)
                        continue;
                    alpha[i] += y.get(i) * y.get(j) * (alphaJOld - alpha[j]);

                    // Actualiza bias
                    double b1 = b - Ei - y.get(i) * (alpha[i] - alphaIOld) * K[i][i]
                                      - y.get(j) * (alpha[j] - alphaJOld) * K[i][j];
                    double b2 = b - Ej - y.get(i) * (alpha[i] - alphaIOld) * K[i][j]
                                      - y.get(j) * (alpha[j] - alphaJOld) * K[j][j];
                    if (0 < alpha[i] && alpha[i] < C)
                        b = b1;
                    else if (0 < alpha[j] && alpha[j] < C)
                        b = b2;
                    else
                        b = (b1 + b2) / 2.0;

                    numChanged++;
                }
            }
            if (numChanged == 0)
                passes++;
            else
                passes = 0;
            
            if (passes % 10 == 0) {
                //System.out.println("SMO passes: " + passes);
                System.out.printf("SMO passes: %d, numChanged: %d%n", passes, numChanged);
            }
            iteration++;
        }

        // Guarda support vectors, alphas, labels y bias
        model.clearSupportVectors(); // (método para vaciar si lo deseas)
        for (int i = 0; i < N; i++) {
            if (alpha[i] > 1e-6) {
                model.addSupportVector(X.get(i), alpha[i], y.get(i));
            }
        }
        model.setBias(b);

        return model;
    }

    private double predictRaw(double[] alpha, List<Double> y, double[][] K, double b, int i) {
        double sum = 0.0;
        for (int j = 0; j < alpha.length; j++)
            sum += alpha[j] * y.get(j) * K[j][i];
        return sum + b;
    }

    private double clip(double val, double low, double high) {
        return Math.max(low, Math.min(val, high));
    }
}
