package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/** Pops 1 Signal, returns its first‐difference derivative. */
public class DerivativeOp implements RPNOperation {
    @Override public int arity() { return 1; }
    @Override
    public Object apply(List<Object> args) {
        Signal s         = (Signal) args.get(0);
        List<Double> d   = SignalProcessor.derivative(s.getValues());
        return new Signal(d);
    }
    
    // DerivativeOp.java
    @Override public String getName() { return "deriv"; }
    @Override public String getDescription() { return "Computes the numerical derivative of a signal."; }
    @Override public String getExample() { return "sig1 deriv"; }
    @Override public String getCategory() { return "Transform"; }
}
