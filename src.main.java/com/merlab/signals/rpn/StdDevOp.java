package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/** Pops 1 Signal, pushes its standard deviation. */
public class StdDevOp implements RPNOperation {
    @Override public int arity() { return 1; }
    @Override
    public Object apply(List<Object> args) {
        @SuppressWarnings("unchecked")
        Signal s       = (Signal) args.get(0);
        return StatisticalProcessor.stdDev(s);
    }
    
    // StdDevOp.java
    @Override public String getName() { return "std"; }
    @Override public String getDescription() { return "Computes the standard deviation of a signal."; }
    @Override public String getExample() { return "sig1 std"; }
    @Override public String getCategory() { return "Statistics"; }
}
