package com.merlab.signals.data.synthetic;

import java.util.Random;

/**
 * Genera un array de filas [x, y, label],
 * donde (x,y) cae en uno de dos círculos concéntricos:
 *  label=0 → radio ≤ rInt
 *  label=1 → rInt < radio ≤ rExt
 */
public class SyntheticCirclesDataset {
    public static double[][] generate(int n, double rInt, double rExt) {
        Random rnd = new Random(0);
        double[][] data = new double[n][3];
        for (int i = 0; i < n; i++) {
            // Elige un círculo aleatoriamente
            boolean inner = rnd.nextBoolean();
            double radius = inner 
                ? rInt * rnd.nextDouble() 
                : rInt + (rExt - rInt) * rnd.nextDouble();
            double angle  = 2 * Math.PI * rnd.nextDouble();
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            int label = inner ? 0 : 1;
            data[i] = new double[]{ x, y, label };
        }
        return data;
    }
}
