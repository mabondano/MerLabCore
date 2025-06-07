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
