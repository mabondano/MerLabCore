package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/** Pops [Signal, Double(alpha)], applies single‐pole high‐pass filter. */
public class HighPassFilterOp implements RPNOperation {
    @Override public int arity() { return 2; }
    @Override
    public Object apply(List<Object> args) {
        Signal s         = (Signal) args.get(0);
        double alpha     = ((Double) args.get(1)).doubleValue();
        List<Double> out = SignalProcessor.highPassFilter(s.getValues(), alpha);
        return new Signal(out);
    }
    

 // HighPassFilterOp.java
 @Override public String getName() { return "hpf"; }
 @Override public String getDescription() { return "Applies a high-pass filter to a signal."; }
 @Override public String getExample() { return "sig1 cutoff hpf"; }
 @Override public String getCategory() { return "Filter"; }
}
