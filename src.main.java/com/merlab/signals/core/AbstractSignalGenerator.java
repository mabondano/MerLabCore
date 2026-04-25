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
