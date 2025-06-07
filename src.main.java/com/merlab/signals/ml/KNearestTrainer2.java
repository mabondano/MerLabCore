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

import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.trainer.Trainer;
import com.merlab.signals.nn.trainer.Trainer2;

public class KNearestTrainer2 implements Trainer2<KNearestProcessor2> {
    @Override
    public KNearestProcessor2 train(KNearestProcessor2 model, DataSet data, int epochs, double lr) {
        // Para KNN no hay nada que actualizar: devolvemos el mismo objeto.
        return model;
    }
}
