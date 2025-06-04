package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.ml.SVMProcessor2;

import java.util.Arrays;
import java.util.List;

public class SVMExampleMini {
    public static void main(String[] args) {
        // 1. Creamos un SVM lineal para 2 features (2D)
        SVMProcessor2 svm = new SVMProcessor2(2);

        // 2. Establecemos pesos y bias manualmente (ejemplo: w = [1, -1], b = 0)
        svm.setWeights(new double[]{1.0, -1.0});
        svm.setBias(0.0);

        // 3. Creamos puntos sintéticos como Signal (List<Double>)
        Signal p1 = new Signal(Arrays.asList(2.0, 1.0)); // Esperamos +1 (pues 2 - 1 + 0 = 1 > 0)
        Signal p2 = new Signal(Arrays.asList(1.0, 3.0)); // Esperamos -1 (1 - 3 + 0 = -2 < 0)
        Signal p3 = new Signal(Arrays.asList(3.0, 0.5)); // Esperamos +1

        List<Signal> points = Arrays.asList(p1, p2, p3);

        // 4. Predecimos y mostramos los resultados
        for (int i = 0; i < points.size(); i++) {
            Signal pred = svm.predict(points.get(i));
            System.out.printf("Point %d: %s -> Predicted class: %.1f%n",
                    i + 1, points.get(i).getValues(), pred.get(0));
        }
    }
}
