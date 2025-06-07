/*
 * Copyright 2025 Merly Abondano
 *
 * Created:   2025-06-07
 * Author:    Merly Abondano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.merlab.signals.ml;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.trainer.Trainer2;

import java.util.List;

public class SVMTrainer2 implements Trainer2<SVMProcessor2> {

    private final double C = 0.01; // Parámetro de regularización, puedes exponerlo en constructor si quieres

    @Override
    public SVMProcessor2 train(SVMProcessor2 initial, DataSet data, int epochs, double learningRate) {
        int n = initial.getWeights().length;
        double[] w = initial.getWeights().clone();
        double b = initial.getBias();

        List<Signal> signals = data.getInputs(); // cada signal es un vector de tamaño n
        List<Signal> targets = data.getTargets(); // cada target es un signal con un solo valor (la clase)

        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < signals.size(); i++) {
                Signal sig = signals.get(i);
                double[] x = sig.getValues().stream().mapToDouble(Double::doubleValue).toArray();
                double y = targets.get(i).get(0); // target: +1.0 o -1.0
                double decision = dot(w, x) + b;

                // SVM hinge loss subgradiente
                if (y * decision < 1) {
                    for (int j = 0; j < n; j++)
                        w[j] += learningRate * (y * x[j] - 2 * C * w[j]);
                    b += learningRate * y;
                } else {
                    for (int j = 0; j < n; j++)
                        w[j] += -learningRate * 2 * C * w[j];
                    // b no cambia
                }
            }
        }
        initial.setWeights(w);
        initial.setBias(b);
        return initial;
    }

    private double dot(double[] w, double[] x) {
        double s = 0;
        for (int i = 0; i < w.length; i++) s += w[i]*x[i];
        return s;
    }
}
