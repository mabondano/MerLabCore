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

package com.merlab.signals.core;

import java.util.Random;

/**
 * Clase base para generadores de señales.
 * Gestiona los parámetros compartidos (size, RNG).
 */
public abstract class AbstractSignalGenerator implements SignalProvider {
    protected final int size;
    protected final Random rng;

    public AbstractSignalGenerator(int size, Long seed) {
        this.size = size;
        this.rng  = (seed != null) ? new Random(seed) : new Random();
    }

    public AbstractSignalGenerator(int size) {
        this(size, null);
    }

    // Cada subclase implementa su propia generación
    @Override
    public abstract Signal getSignal();
}
