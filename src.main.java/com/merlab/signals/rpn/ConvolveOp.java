package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/** Pops [Signal A, Signal B], pushes their linear convolution A * B. */
public class ConvolveOp implements RPNOperation {
    @Override public int arity() { return 2; }
    
    @Override
    public Object apply(List<Object> args) {
        Signal a        = (Signal) args.get(0);
        Signal b        = (Signal) args.get(1);
        List<Double> c  = SignalProcessor.convolve(a.getValues(), b.getValues());
        return new Signal(c);
    }
    
    // ConvolveOp.java
    @Override public String getName() { return "conv"; }
    @Override public String getDescription() { return "Convolves a signal with a kernel."; }
    @Override public String getExample() { return "sig1 kernel conv"; }
    @Override public String getCategory() { return "Transform"; }
}
