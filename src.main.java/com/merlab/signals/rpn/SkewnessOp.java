package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/** Pops 1 Signal, pushes its skewness (Double). */
public class SkewnessOp implements RPNOperation {
    @Override public int arity() { return 1; }
    @Override
    public Object apply(List<Object> args) {
        @SuppressWarnings("unchecked")
        Signal s       = (Signal) args.get(0);
        return StatisticalProcessor.skewness(s);
    }
    
    // SkewnessOp.java
    @Override public String getName() { return "skew"; }
    @Override public String getDescription() { return "Computes the skewness of a signal."; }
    @Override public String getExample() { return "sig1 skew"; }
    @Override public String getCategory() { return "Statistics"; }
}
