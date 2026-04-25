package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/** Pops 1 Signal, pushes its maximum value (Double). */
public class MaxOp implements RPNOperation {
    @Override public int arity() { return 1; }
    
    @Override
    public Object apply(List<Object> args) {
        @SuppressWarnings("unchecked")
        //List<Double> s = (List<Double>) args.get(0);
        Signal s = (Signal) args.get(0);
        return StatisticalProcessor.max(s);
    }
    
    // MaxOp.java
    @Override public String getName() { return "max"; }
    @Override public String getDescription() { return "Returns the maximum value of a signal."; }
    @Override public String getExample() { return "sig1 max"; }
    @Override public String getCategory() { return "Statistics"; }
}
