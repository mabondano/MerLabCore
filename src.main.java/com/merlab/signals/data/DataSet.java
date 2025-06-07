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

package com.merlab.signals.data;

import com.merlab.signals.core.Signal;
import java.util.List;

/** Encapsula listas de señales de entrada y señales objetivo. */
public class DataSet {
    private final List<Signal> inputs;
    private final List<Signal> targets;

    public DataSet(List<Signal> inputs, List<Signal> targets) {
        this.inputs  = inputs;
        this.targets = targets;
    }
    public List<Signal> getInputs() { return inputs; }
    public List<Signal> getTargets() { return targets; }
}
