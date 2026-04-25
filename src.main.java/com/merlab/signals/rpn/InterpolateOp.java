package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/**
 * Binary RPN op: [Signal, Double(factor)] INTERPOLATE
 */
public class InterpolateOp implements RPNOperation {

    @Override public int arity() { return 2; }

    @Override
    public Object apply(List<Object> args) {
        Signal s       = (Signal) args.get(0);
        int factor     = ((Double) args.get(1)).intValue();
        List<Double> ip = SignalProcessor.interpolate(s.getValues(), factor);
        return new Signal(ip);
    }
    
    // IntegrateOp.java
    @Override public String getName() { return "intg"; }
    @Override public String getDescription() { return "Computes the numerical integral (cumulative sum) of a signal."; }
    @Override public String getExample() { return "sig1 intg"; }
    @Override public String getCategory() { return "Transform"; }
}
