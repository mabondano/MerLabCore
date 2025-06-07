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

package com.merlab.signals.reporter;

/**
 * Imprime por consola un resumen de ModelInfo.
 * - Para redes: capas, epochs, lr, mse, accuracy, R².
 * - Para clustering: centroides (si existen).
 */
public class ModelReporter {
	
    public static void report(ModelInfo info) {
    	System.out.println("=== Modelo: " + info.getModelName() + " ===");
        
    	// 1) Si hay capas, las listamos (p.ej. para redes neuronales)
        if (!info.getLayers().isEmpty()) {
            for (int i = 0; i < info.getLayers().size(); i++) {
                ModelInfo.LayerInfo L = info.getLayers().get(i);
                System.out.printf(
                    "  Capa %d: %d → %d  (act: %s)%n",
                    i + 1, L.inputs, L.outputs, L.activation
                );
            }
        }

        // 2) Parámetros generales (epochs + learningRate)
        System.out.printf(
            "  Épocas: %d, LearningRate: %.5f%n",
            info.getEpochs(),
            info.getLearningRate()
        );

        // 3) MSE (si existe)
        System.out.printf("  MSE: %.6f%n", info.getMse());
        
        // 3) MSE (si existe)
        System.out.printf("  MaxIter: %d %n", info.getMaxIterations());

        // 4) Accuracy (solo si se definió)
        if (info.hasAccuracy()) {
            System.out.printf("  Accuracy: %.2f%%%n", info.getAccuracy() * 100.0);
        }

        // 5) R² (solo si se definió)
        if (info.hasR2()) {
            System.out.printf("  R²: %.4f%n", info.getR2());
        }
        System.out.println("  ");

        // 6) Centroides (solo si hay)
        if (info.hasCentroids()) {
            System.out.println("  Centroides encontrados:");
            for (int c = 0; c < info.getCentroids().size(); c++) {
                double[] centroide = info.getCentroids().get(c);
                // imprimir arreglo con formato [a1, a2, ..., an]
                System.out.print("    C" + (c + 1) + ": [");
                for (int d = 0; d < centroide.length; d++) {
                    System.out.printf("%.6f", centroide[d]);
                    if (d < centroide.length - 1) System.out.print(", ");
                }
                System.out.println("]");
            }
        }      

        System.out.println("==============================");
    }
}
