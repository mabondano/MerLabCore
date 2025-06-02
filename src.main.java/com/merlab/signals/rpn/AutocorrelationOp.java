package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/** Pops 1 Signal, pushes its autocorrelation sequence (List<Double>). */
public class AutocorrelationOp implements RPNOperation {
    @Override public int arity() { return 2; }
    
    @Override
    public Object apply(List<Object> args) {
        @SuppressWarnings("unchecked")
        Signal s = (Signal) args.get(0);
        int lag  = (int) args.get(1);
        return StatisticalProcessor.autocorrelation(s, lag);
    }
    
    // AutocorrelationOp.java
    @Override public String getName() { return "acor"; }
    @Override public String getDescription() { return "Computes the autocorrelation of a signal."; }
    @Override public String getExample() { return "sig1 acor"; }
    @Override public String getCategory() { return "Statistics"; }
}
