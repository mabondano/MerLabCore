package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/**
 * Binary RPN op: [Signal, Double(factor)] DECIMATE
 * → takes every n­th sample.
 */
public class DecimateOp implements RPNOperation {

    @Override public int arity() { return 2; }

    @Override
    public Object apply(List<Object> args) {
        Signal s       = (Signal) args.get(0);
        int factor     = ((Double) args.get(1)).intValue();
        List<Double> dec = SignalProcessor.decimate(s.getValues(), factor);
        return new Signal(dec);
    }
    
    // DecimateOp.java
    @Override public String getName() { return "dec"; }
    @Override public String getDescription() { return "Decimates a signal by taking every n-th sample."; }
    @Override public String getExample() { return "sig1 2 dec"; }
    @Override public String getCategory() { return "Transform"; }
}
