package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/** Pops 1 Signal, pushes its kurtosis (Double). */
public class KurtosisOp implements RPNOperation {
    @Override public int arity() { return 1; }
    
    @Override
    public Object apply(List<Object> args) {
        @SuppressWarnings("unchecked")
        //List<Double> s = (List<Double>) args.get(0);
        Signal s = (Signal) args.get(0);
        return StatisticalProcessor.kurtosis(s);
    }
    
    // KurtosisOp.java
    @Override public String getName() { return "kurt"; }
    @Override public String getDescription() { return "Computes the kurtosis of a signal."; }
    @Override public String getExample() { return "sig1 kurt"; }
    @Override public String getCategory() { return "Statistics"; }
}
