// src/main/java/com/merlab/signals/nn/processor/ActivationFunction.java
package com.merlab.signals.nn.processor;

/**
 * Contrato para una función de activación en una neurona.
 */
@FunctionalInterface
public interface ActivationFunction {
    /** Aplica la función de activación a x y devuelve el resultado. */
    double apply(double x);
}
