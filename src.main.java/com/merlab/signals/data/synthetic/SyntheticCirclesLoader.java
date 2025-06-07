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

import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;

public class SyntheticCirclesLoader implements DataLoader {
    private final int n;
    private final double rInt;
    private final double rExt;

    public SyntheticCirclesLoader(int n, double rInt, double rExt) {
        this.n = n;
        this.rInt = rInt;
        this.rExt = rExt;
    }
    
    @Override
    public DataSet load() {
        // 1) Generar matriz raw con x,y,label
        double[][] raw = com.merlab.nn.examples.SyntheticCirclesDataset.generate(n, rInt, rExt);
        // 2) Convertir a DataSet
        return DataSetBuilder.fromArray(raw);
    }

    @Override
    public String toString() {
        return String.format("SyntheticCircles(n=%d, rInt=%.2f, rExt=%.2f)", n, rInt, rExt);
    }
}