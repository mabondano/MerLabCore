package com.merlab.signals.rpn;

import com.merlab.signals.core.Complex;
import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

import java.util.List;

/**
 * Pops 1 Signal, performs FFT (power‐of‐two length),
 * pushes back a List<Complex> (frequency bins).
 */
public class FFTOp implements RPNOperation {
    @Override public int arity() { return 1; }
    @Override
    public Object apply(List<Object> args) {
        Signal s         = (Signal) args.get(0);
        List<Complex> X  = SignalProcessor.fft(s.getValues());
        return X;  // push the raw Complex list
    }
    
    // FFTOp.java
    @Override public String getName() { return "fft"; }
    @Override public String getDescription() { return "Computes the FFT of a signal (radix-2)."; }
    @Override public String getExample() { return "sig1 fft"; }
    @Override public String getCategory() { return "Transform"; }
}
