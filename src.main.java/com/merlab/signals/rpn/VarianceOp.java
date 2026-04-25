package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/** Pops 1 Signal, pushes its variance (Double). */
public class VarianceOp implements RPNOperation {
    @Override public int arity() { return 1; }
    @Override
    public Object apply(List<Object> args) {
        @SuppressWarnings("unchecked")
        Signal s       = (Signal) args.get(0);
        return StatisticalProcessor.variance(s);
    }
    
    // VarianceOp.java
    @Override public String getName() { return "var"; }
    @Override public String getDescription() { return "Computes the variance of a signal."; }
    @Override public String getExample() { return "sig1 var"; }
    @Override public String getCategory() { return "Statistics"; }
}
