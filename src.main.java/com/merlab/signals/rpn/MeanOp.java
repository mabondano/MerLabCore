package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/** Pops 1 Signal, pushes its mean (Double). */
public class MeanOp implements RPNOperation {
    @Override public int arity() { return 1; }
    
    @Override
    public Object apply(List<Object> args) {
        @SuppressWarnings("unchecked")
        //List<Double> s = (List<Double>) args.get(0);
        Signal s = (Signal) args.get(0);
        return StatisticalProcessor.mean(s);
    }
    
    // MeanOp.java
    @Override public String getName() { return "mean"; }
    @Override public String getDescription() { return "Computes the mean (average) of a signal."; }
    @Override public String getExample() { return "sig1 mean"; }
    @Override public String getCategory() { return "Statistics"; }
}
