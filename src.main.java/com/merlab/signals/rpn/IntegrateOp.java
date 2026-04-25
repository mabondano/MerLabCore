package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/** Pops 1 Signal, returns its cumulative sum (integral). */
public class IntegrateOp implements RPNOperation {
    @Override public int arity() { return 1; }
    @Override
    public Object apply(List<Object> args) {
        Signal s       = (Signal) args.get(0);
        List<Double> i = SignalProcessor.integrate(s.getValues());
        return new Signal(i);
    }
    
    // IntegrateOp.java
    @Override public String getName() { return "intg"; }
    @Override public String getDescription() { return "Computes the numerical integral (cumulative sum) of a signal."; }
    @Override public String getExample() { return "sig1 intg"; }
    @Override public String getCategory() { return "Transform"; }
}
