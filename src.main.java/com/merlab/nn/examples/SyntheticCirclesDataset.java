package com.merlab.nn.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.merlab.signals.core.Signal;

/**
 * Ejemplo de generación de un dataset sintético de dos clases
 * no linealmente separables: círculos concéntricos.
 */
public class SyntheticCirclesDataset {

    /**
     * Genera puntos en dos círculos concéntricos:
     * clase 0 en radio interior, clase 1 en anillo exterior.
     * @param nPuntos número total de puntos (dividido entre ambas clases)
     * @param radioInt radio del círculo interior
     * @param radioExt radio del círculo exterior
     * @return arreglo de pares [x, y, label]
     */
    public static double[][] generate(int nPuntos, double radioInt, double radioExt) {
        Random rnd = new Random();
        int nHalf = nPuntos / 2;
        double[][] data = new double[nPuntos][3];
        
        // Clase 0: círculo interior
        for (int i = 0; i < nHalf; i++) {
            double theta = rnd.nextDouble() * 2 * Math.PI;
            double r = radioInt * Math.sqrt(rnd.nextDouble());
            data[i][0] = r * Math.cos(theta);
            data[i][1] = r * Math.sin(theta);
            data[i][2] = 0;
        }
        // Clase 1: anillo exterior
        for (int i = nHalf; i < nPuntos; i++) {
            double theta = rnd.nextDouble() * 2 * Math.PI;
            double r = radioExt * (0.5 + rnd.nextDouble() * 0.5);
            data[i][0] = r * Math.cos(theta);
            data[i][1] = r * Math.sin(theta);
            data[i][2] = 1;
        }
        return data;
    }

    public static void main(String[] args) {
        double[][] dataset = generate(200, 1.0, 2.5);
        // Separar en señales y etiquetas
        List<Signal> inputs = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        for (double[] row : dataset) {
            Signal in = new Signal();
            in.add(row[0]);
            in.add(row[1]);
            inputs.add(in);
            Signal out = new Signal();
            out.add(row[2]);
            targets.add(out);
        }
        // Imprimir primeros 5 puntos
        for (int i = 0; i < 5; i++) {
            System.out.printf("[%.3f, %.3f] -> %d\n",
                dataset[i][0], dataset[i][1], (int) dataset[i][2]);
        }
    }
}

/*
 * 
data.length == nPuntos → número de filas.

data[i].length == 3 → número de columnas (aquí: x, y y label).

*/
