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

import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.trainer.Trainer2;

public class KernelPerceptronTrainer implements Trainer2<SVMProcessor2> {
    private final int epochs;
    private final double gamma;

    public KernelPerceptronTrainer(int epochs, double gamma) {
        this.epochs = epochs;
        this.gamma = gamma;
    }

    @Override
    public SVMProcessor2 train(SVMProcessor2 model, DataSet data, int ignored, double ignored2) {
        model.setKernel(SVMProcessor2.KernelType.RBF);
        model.setGamma(gamma);
        
        int M = data.getInputs().size();


        List<double[]> X = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            double[] xi = data.getInputs().get(i).getValues().stream().mapToDouble(Double::doubleValue).toArray();
            X.add(xi);
            y.add(data.getTargets().get(i).get(0));
        }

        int N = X.size();
        double[] alpha = new double[N]; // Coeficientes

        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < N; i++) {
                double sum = 0.0;
                for (int j = 0; j < N; j++)
                    sum += alpha[j] * y.get(j) * model.kernel(X.get(j), X.get(i));
                double pred = sum; // sin bias
                if (y.get(i) * pred <= 0)
                    alpha[i] += 1.0;
            }
        }

        // Guarda los support vectors y alphas en el modelo
        for (int i = 0; i < N; i++) {
            if (alpha[i] > 0) {
            	model.addSupportVector(X.get(i), alpha[i], y.get(i));
                //model.supportVectors.add(X.get(i));
                //model.alphas.add(alpha[i]);
                //model.supportLabels.add(y.get(i));
            }
        }
        return model;
    }
}
