package com.merlab.signals.core;

/**
 * Una fuente de señales, que puede cargar desde BD o generar sintéticamente.
 */
//1) La interfaz común
public interface SignalProvider {
    /**
     * Devuelve una señal completa para procesar.
     * @return la señal obtenida
     */
    Signal getSignal();

}